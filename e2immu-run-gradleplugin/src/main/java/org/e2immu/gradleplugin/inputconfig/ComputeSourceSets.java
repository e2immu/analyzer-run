package org.e2immu.gradleplugin.inputconfig;

import org.e2immu.language.cst.api.element.SourceSet;
import org.e2immu.language.inspection.resource.SourceSetImpl;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.internal.artifacts.DefaultProjectComponentIdentifier;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ComputeSourceSets {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeSourceSets.class);

    public record Result(String mainSourceSetName, Map<String, SourceSet> sourceSetsByName,
                         List<Result> sourceSetDependencies) {
    }

    public Result compute(Project project,
                          String restrictSourcesToPackages,
                          String restrictTestSourcesToPackages,
                          Set<String> excludeFromClasspath) {
        Result result = compute(project, restrictSourcesToPackages, restrictTestSourcesToPackages, excludeFromClasspath,
                new HashSet<>());
        LOGGER.info("Exit compute source sets with result");
        return result;
    }

    private Result compute(Project project,
                           String restrictSourcesToPackages,
                           String restrictTestSourcesToPackages,
                           Set<String> excludeFromClasspath,
                           Set<String> projectsSeen) {
        projectsSeen.add(project.getName());
        LOGGER.info("Computing project {}, seen is {}", project.getName(), projectsSeen);

        String encoding = detectSourceEncoding(project);
        JavaPluginExtension javaPluginExtension = new DslObject(project).getExtensions().getByType(JavaPluginExtension.class);
        Map<String, org.e2immu.language.cst.api.element.SourceSet> sourceSetsByName = new HashMap<>();
        String projectName = project.getName();
        String mainSourceSetName = projectName + "/main";
        SourceSet mainSourceSet = makeSourceSet(javaPluginExtension, mainSourceSetName, restrictSourcesToPackages, encoding, false);
        if (mainSourceSet != null) sourceSetsByName.put(mainSourceSet.name(), mainSourceSet);
        String testSourceSetName = projectName + "/test";
        SourceSet testSourceSet = makeSourceSet(javaPluginExtension, testSourceSetName, restrictTestSourcesToPackages, encoding, true);
        if (testSourceSet != null) sourceSetsByName.put(testSourceSet.name(), testSourceSet);

        List<Result> sourceSetDependencies = new ArrayList<>();
        for (Configuration configuration : project.getConfigurations()) {
            if (configuration.isCanBeResolved()) {
                String configurationName = configuration.getName();
                LOGGER.info(" -- analyzing configuration {}, {} dependencies", configurationName, configuration.getAllDependencies().size());
                boolean isTest = configurationName.toLowerCase().contains("test");
                boolean isRuntimeOnly = configurationName.toLowerCase().contains("runtimeonly");

                for (ResolvedArtifactResult rar : configuration.getIncoming().getArtifacts().getArtifacts()) {
                    if (rar.getVariant().getOwner() instanceof ModuleComponentIdentifier mci) {
                        String description = mci.getGroup() + ":" + mci.getModule() + ":" + mci.getVersion();
                        LOGGER.info(" -- library dependency {} in {}", description, configurationName);
                        if (!sourceSetsByName.containsKey(description)) {
                            File file = rar.getFile();
                            if (file.canRead() && !excludeFromClasspath.contains(file.getName())
                                && !excludeFromClasspath.contains(description)
                                && !excludeFromClasspath.contains(mci.getModule())) {
                                org.e2immu.language.cst.api.element.SourceSet set = new SourceSetImpl(description, null,
                                        file.toURI(), null, isTest, true, true, false,
                                        isRuntimeOnly, null, null);
                                sourceSetsByName.put(description, set);
                            }
                        }
                    } else if (rar.getVariant().getOwner() instanceof DefaultProjectComponentIdentifier pci) {
                        String description = pci.getProjectName();
                        LOGGER.info(" --  project dependency {} in configuration {}, looking for path {}", description, configurationName, pci.getProjectPath());
                        Project dependentProject = findProject(project, pci.getProjectName());
                        if (dependentProject != null && !dependentProject.equals(project)) {
                            if (!projectsSeen.contains(description)) {
                                Result result = compute(dependentProject, null,
                                        null, excludeFromClasspath, projectsSeen);
                                sourceSetDependencies.add(result);
                            }
                        }
                    }
                }
            }
        }
        LOGGER.info("Project {} has source set names {}, {} dependencies", mainSourceSet, sourceSetsByName.keySet(),
                sourceSetDependencies.size());
        return new Result(mainSourceSetName, sourceSetsByName, sourceSetDependencies);
    }

    private Project findProject(Project project, String projectName) {
        Project local = project.getRootProject().getAllprojects().stream()
                .peek(p -> LOGGER.info("other project: {}", p.getName()))
                .filter(p -> p.getName().equals(projectName)).findFirst().orElse(null);
        if (local != null) return local;
        project.getGradle().getIncludedBuilds().stream()
                .peek(b -> LOGGER.info("other build: {}", b.getName()))
                .filter(b -> b.getName().equals(projectName))
                .findFirst().ifPresent(includedBuild -> LOGGER.info("Found included build"));
        return null;
    }


    private SourceSet makeSourceSet(JavaPluginExtension javaPluginExtension,
                                    String e2immuSourceSetName,
                                    String restrictTo,
                                    String encodingString,
                                    boolean test) {
        Set<String> restrictToPackages = restrictTo == null || restrictTo.isBlank() ? null :
                Arrays.stream(restrictTo.split("[,;]\\s*"))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toUnmodifiableSet());
        Charset sourceEncoding = encodingString == null ? null : Charset.forName(encodingString);
        String gradleSourceSetName = test ? "test" : "main";
        org.gradle.api.tasks.SourceSet gradleSourceSet = javaPluginExtension.getSourceSets().getByName(gradleSourceSetName);
        List<Path> paths = gradleSourceSet.getAllJava().getSrcDirs().stream()
                .filter(File::canRead).map(File::toPath).toList();
        if (paths.isEmpty()) return null;
        Path path = paths.get(0);
        return new SourceSetImpl(e2immuSourceSetName, paths, path.toUri(),
                sourceEncoding, test, false, false, false, false,
                restrictToPackages, null);
    }

    private static String detectSourceEncoding(Project project) {
        AtomicReference<String> encodingRef = new AtomicReference<>();
        project.getTasks().withType(JavaCompile.class, compile -> {
            String encoding = compile.getOptions().getEncoding();
            if (encoding != null) {
                encodingRef.set(encoding);
            }
        });
        return encodingRef.get();
    }
}
