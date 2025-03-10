package org.e2immu.gradleplugin.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.run.main.Main;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class WriteInputConfigurationTask extends ConventionTask {
    private static final Logger LOGGER = Logging.getLogger(AnalyzerTask.class);
    private Map<String, String> analyserProperties;

    @TaskAction
    public void run() {
        Map<String, String> properties = getProperties();

        Configuration configuration = Main.fromPropertyMap(properties);
        File buildDir = getProject().getLayout().getBuildDirectory().getAsFile().get();
        File outFile = new File(buildDir, "inputConfiguration.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String s = objectMapper.writerFor(InputConfigurationImpl.class).withDefaultPrettyPrinter()
                    .writeValueAsString(configuration.inputConfiguration());
            Files.writeString(outFile.toPath(), s);

            writeDocker(buildDir, configuration.inputConfiguration());
        } catch (IOException ioException) {
            LOGGER.error("Caught IOException", ioException);
        }
    }

    /**
     * @return The String key/value pairs to be passed to the analyser.
     * {@code null} values are not permitted.
     */
    @Input
    public Map<String, String> getProperties() {
        if (analyserProperties == null) {
            analyserProperties = new LinkedHashMap<>();
        }
        return analyserProperties;
    }


    public static final String HOME_UBUNTU_PROJECTS = "/home/ubuntu/projects/";
    public static final String DOCKER_PREFIX = """
            FROM localhost/codelaser/refactor:latest
            USER ubuntu
            WORKDIR /home/ubuntu
            """;

    public static final String PREP_SCRIPT_PREFIX = """
            #!/bin/bash
            mkdir -p build/docker/libs
            """;
    public static final String DOCKER_INPUT_CONFIGURATION_JSON = "dockerInputConfiguration.json";
    public static final String PROJECT_PROPERTIES = "project.properties";
    public static final String DOCKERFILE = "Dockerfile";
    public static final String PREP_SCRIPT = "moveLibs.sh";


    private void writeDocker(File buildDir, InputConfiguration inputConfiguration) throws IOException {
        InputConfigurationImpl.Builder builder = new InputConfigurationImpl.Builder()
                .setSourceEncoding(inputConfiguration.sourceEncoding().displayName());
        String project = getProject().getName();

        File gitRootFile = findGitRoot(getProject().getRootDir());
        String gitRoot = gitRootFile.getAbsolutePath();

        File buildDocker = new File(buildDir, "docker");
        if (buildDocker.mkdirs()) {
            LOGGER.debug("Created {}", buildDocker);
        }

        String buildDockerPath = buildDocker.getAbsolutePath() + "/";
        String relativeBuildDockerPath = buildDockerPath.substring(gitRoot.length() + 1);

        Set<String> copied = new HashSet<>();
        File dockerFile = new File(buildDocker, DOCKERFILE);
        File prepScript = new File(buildDocker, PREP_SCRIPT);
        try (OutputStreamWriter dockerfileWriter = new OutputStreamWriter(new FileOutputStream(dockerFile));
             OutputStreamWriter prepScriptWriter = new OutputStreamWriter(new FileOutputStream(prepScript))) {

            dockerfileWriter.append(DOCKER_PREFIX);
            prepScriptWriter.append(PREP_SCRIPT_PREFIX);

            dockerfileWriter.append("RUN mkdir -p " + HOME_UBUNTU_PROJECTS).append(project).append("/git ")
                    .append(HOME_UBUNTU_PROJECTS).append(project).append("/libs\n");
            String dockerGitDir = HOME_UBUNTU_PROJECTS + project + "/git";
            dockerfileWriter.append("COPY --chown=ubuntu . ").append(dockerGitDir).append("\n");

            int dirIndex = 0;

            // source path

            for (String path : inputConfiguration.sources()) {
                if (!path.startsWith(gitRoot)) {
                    throw new UnsupportedOperationException("Input sources must be in the git root dir " + gitRoot);
                }
                String rest = path.substring(gitRoot.length());
                String prepended = HOME_UBUNTU_PROJECTS + project + "/git" + rest;
                builder.addSources(prepended);
            }

            for (String path : inputConfiguration.classPathParts()) {
                File file = new File(path);
                if (file.isDirectory()) {
                    if (path.startsWith(gitRoot)) {
                        String rest = path.substring(gitRoot.length());
                        String prepended = HOME_UBUNTU_PROJECTS + project + "/git" + rest;
                        builder.addClassPath(prepended);
                    } else {
                        String libName = "dir" + dirIndex + ".jar";
                        prepScriptWriter.append("jar cf ").append(buildDockerPath).append("libs/").append(libName)
                                .append(" ").append(path).append("\n");
                        String target = HOME_UBUNTU_PROJECTS + project + "/libs/" + libName;
                        dockerfileWriter.append("COPY ").append(relativeBuildDockerPath).append("libs/").append(libName)
                                .append(" ").append(target).append("\n");
                        builder.addClassPath(target);
                    }
                } else if (copied.add(path)) {
                    String fileName = file.getName();
                    prepScriptWriter.append("cp ").append(path).append(" ")
                            .append(buildDockerPath).append("libs/").append(fileName).append("\n");
                    String target = HOME_UBUNTU_PROJECTS + project + "/libs/" + fileName;
                    dockerfileWriter.append("COPY ").append(relativeBuildDockerPath).append("libs/").append(fileName)
                            .append(" ").append(target).append("\n");
                    builder.addClassPath(target);
                }
            }
            // input configuration

            InputConfiguration newInputConfiguration = builder.build();
            try (OutputStreamWriter w = new OutputStreamWriter(
                    new FileOutputStream(buildDockerPath + DOCKER_INPUT_CONFIGURATION_JSON))) {
                new ObjectMapper().writerFor(InputConfigurationImpl.class).writeValue(w, newInputConfiguration);
            }
            dockerfileWriter.append("COPY ").append(relativeBuildDockerPath).append(DOCKER_INPUT_CONFIGURATION_JSON)
                    .append(" ").append(HOME_UBUNTU_PROJECTS).append(project).append("/inputConfiguration.json\n");

            // project properties

            Properties properties = new Properties();
            properties.put("jre", "openjdk-17");
            properties.put("project", project);
            File projectPropertiesFile = new File(buildDocker, PROJECT_PROPERTIES);
            try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(projectPropertiesFile))) {
                properties.store(w, "Properties of project " + project);
            }
            dockerfileWriter.append("COPY ").append(relativeBuildDockerPath).append(PROJECT_PROPERTIES)
                    .append(" ").append(HOME_UBUNTU_PROJECTS).append(project).append("/project.properties\n");

            // clean git directory

            dockerfileWriter.append("RUN cd ").append(dockerGitDir).append(" && git restore . && git clean -fd .\n");
        }
    }

    private File findGitRoot(File rootDir) {
        File current = rootDir;
        while (!(new File(current, ".git").isDirectory())) {
            current = current.getParentFile();
            if ("/".equals(current.getAbsolutePath())) {
                throw new UnsupportedOperationException("No git root found");
            }
        }
        return current;
    }
}
