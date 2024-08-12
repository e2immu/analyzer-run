/*
 * e2immu: a static code analyser for effective and eventual immutability
 * Copyright 2020-2021, Bart Naudts, https://www.e2immu.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.e2immu.gradleplugin;

import org.e2immu.analyzer.run.main.Main;
import org.e2immu.util.internal.util.GradleConfiguration;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Property names are identical to those of the CLI (.cli.Main). In the system properties,
 * they have to be prefixed by the PREFIX defined in this class.
 */
public record AnalyzerPropertyComputer(
        Map<String, ActionBroadcast<AnalyzerProperties>> actionBroadcastMap,
        Project targetProject) {

    private static final Logger LOGGER = Logging.getLogger(AnalyzerPropertyComputer.class);
    public static final String PREFIX = "e2immu-analyser.";
    // used for round trip String[] -> String -> String[]; TODO this should be done in a better way
    public static final String M_A_G_I_C = "__M_A_G_I_C__";

    public static final String DEPENDENCIES = "dependencies";


    public Map<String, Object> computeProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        computeProperties(targetProject, properties, "");
        return properties;
    }

    private void computeProperties(Project project, Map<String, Object> properties, String prefix) {
        AnalyzerExtension extension = project.getExtensions().getByType(AnalyzerExtension.class);
        if (extension.skipProject) {
            return;
        }
        Map<String, Object> rawProperties = new LinkedHashMap<>();
        detectProperties(project, rawProperties, extension);

        ActionBroadcast<AnalyzerProperties> actionBroadcast = actionBroadcastMap.get(project.getPath());
        if (actionBroadcast != null) {
            AnalyzerProperties analyzerProperties = new AnalyzerProperties(properties);
            actionBroadcast.execute(analyzerProperties);
        }

        // with the highest priority, override directly for this project from the system properties
        if (project.equals(targetProject)) {
            addSystemProperties(rawProperties);
        }
        // convert all the properties from subprojects into dot-notated properties
        flattenProperties(rawProperties, prefix, properties);

        LOGGER.debug("Resulting map is " + properties);

        List<Project> enabledChildProjects = project.getChildProjects().values().stream()
                .filter(p -> !p.getExtensions().getByType(AnalyzerExtension.class).skipProject)
                .toList();

        List<Project> skippedChildProjects = project.getChildProjects().values().stream()
                .filter(p -> p.getExtensions().getByType(AnalyzerExtension.class).skipProject)
                .toList();

        if (!skippedChildProjects.isEmpty()) {
            LOGGER.debug("Skipping collecting Analyser properties on: " +
                         skippedChildProjects.stream().map(Project::toString).collect(Collectors.joining(", ")));
        }

        // recurse
        for (Project childProject : enabledChildProjects) {
            String moduleId = childProject.getPath();
            String modulePrefix = !prefix.isEmpty() ? (prefix + "." + moduleId) : moduleId;
            computeProperties(childProject, properties, modulePrefix);
        }
    }

    private void detectProperties(Project project, Map<String, Object> properties, AnalyzerExtension extension) {
        // general
        properties.put(Main.INCREMENTAL_ANALYSIS, extension.incrementalAnalysis);
        String analysisResultsDir;
        if (extension.analysisResultsDir != null) {
            analysisResultsDir = extension.analysisResultsDir;
        } else {
            // default value: "${build.dir}/e2immu"
            File buildDir = project.getLayout().getBuildDirectory().get().getAsFile();
            analysisResultsDir = new File(buildDir, "e2immu").getAbsolutePath();
        }
        properties.put(Main.ANALYSIS_RESULTS_DIR, analysisResultsDir);
        properties.put(Main.PARALLEL, extension.parallel);
        properties.put(Main.ANALYSIS_STEPS, extension.analysisSteps);
        properties.put(Main.DEBUG, extension.debugTargets);
        properties.put(Main.QUIET, extension.quiet);

        // input
        properties.put(Main.SOURCE_PACKAGES, extension.sourcePackages);
        properties.put(Main.TEST_SOURCE_PACKAGES, extension.testSourcePackages);
        properties.put(Main.JRE, extension.jre);

        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            boolean hasSource = detectSourceDirsAndJavaClasspath(project, properties, extension.jmods);
            if (hasSource) {
                detectSourceEncoding(project, properties);
            }
        });

        // Annotated API
        // use case 1
        if (extension.analyzedAnnotatedApiDirs != null) {
            properties.put(Main.ANALYZED_ANNOTATED_API_DIRS, extension.analyzedAnnotatedApiDirs);
        }
        // use case 2
        if (extension.analyzedAnnotatedApiTargetDir != null) {
            properties.put(Main.ANALYZED_ANNOTATED_API_TARGET_DIR, extension.analyzedAnnotatedApiTargetDir);
        }
        // use case 3
        if (extension.annotatedApiTargetDir != null) {
            properties.put(Main.ANNOTATED_API_TARGET_DIR, extension.annotatedApiTargetDir);
        }
        if (extension.annotatedApiTargetPackage != null) {
            properties.put(Main.ANNOTATED_API_TARGET_PACKAGE, extension.annotatedApiTargetPackage);
        }
        if (extension.annotatedApiPackages != null) {
            properties.put(Main.ANNOTATED_API_PACKAGES, extension.annotatedApiPackages);
        }

        // actions
        properties.put(Main.ACTION, extension.action);
        if (extension.actionParameters != null) {
            String joined = String.join(M_A_G_I_C, extension.actionParameters);
            properties.put(Main.ACTION_PARAMETER, joined);
        }
    }

    private static String getOrDefault(String property, String defaultValue) {
        return property == null || property.isBlank() ? defaultValue : property;
    }

    private static void detectSourceEncoding(Project project, final Map<String, Object> properties) {
        project.getTasks().withType(JavaCompile.class, compile -> {
            String encoding = compile.getOptions().getEncoding();
            if (encoding != null) {
                properties.put(Main.SOURCE_ENCODING, encoding);
            }
        });
    }

    private static final String[] UNRESOLVABLE_CONFIGURATIONS =
            Arrays.stream(GradleConfiguration.values()).filter(c -> !c.transitive)
                    .map(c -> c.gradle).toArray(String[]::new);

    private static final String[] RESOLVABLE_CONFIGURATIONS =
            Arrays.stream(GradleConfiguration.values()).filter(c -> c.transitive)
                    .map(c -> c.gradle).toArray(String[]::new);

    private static final Map<String, String> CONFIG_SHORTHAND =
            Arrays.stream(GradleConfiguration.values()).collect(Collectors.toUnmodifiableMap(
                    c -> c.gradle, c -> c.abbrev
            ));

    private static boolean detectSourceDirsAndJavaClasspath(Project project, Map<String, Object> properties, String jmods) {
        JavaPluginExtension javaPluginExtension = new DslObject(project).getExtensions().getByType(JavaPluginExtension.class);

        SourceSet main = javaPluginExtension.getSourceSets().getAt("main");
        String sourceDirectoriesPathSeparated = sourcePathFromSourceSet(main);
        properties.put(Main.SOURCE, sourceDirectoriesPathSeparated);

        SourceSet test = javaPluginExtension.getSourceSets().getAt("test");
        String testDirectoriesPathSeparated = sourcePathFromSourceSet(test);
        properties.put(Main.TEST_SOURCE, testDirectoriesPathSeparated);

        String jmodsSeparated;
        if (jmods == null || jmods.trim().isEmpty()) jmodsSeparated = "";
        else {
            jmodsSeparated = Arrays.stream(jmods.trim().split("[," + File.pathSeparator + "]"))
                    .map(s -> File.pathSeparator + "jmods/" + s)
                    .collect(Collectors.joining());
        }
        String classPathSeparated = jmodsSeparated + File.pathSeparator + librariesFromSourceSet(main);
        properties.put(Main.CLASSPATH, classPathSeparated);

        String runtimeClassPathSeparated = runtimeLibrariesFromSourceSet(main);
        properties.put(Main.RUNTIME_CLASSPATH, runtimeClassPathSeparated);

        String testClassPathSeparated = librariesFromSourceSet(test);
        properties.put(Main.TEST_CLASSPATH, jmodsSeparated + File.pathSeparator + testClassPathSeparated);

        String testRuntimeClassPathSeparated = runtimeLibrariesFromSourceSet(test);
        properties.put(Main.TESTS_RUNTIME_CLASSPATH, testRuntimeClassPathSeparated);

        List<String> dependencyList = new LinkedList<>();
        Set<String> seen = new HashSet<>();
        for (String configurationName : UNRESOLVABLE_CONFIGURATIONS) {
            Configuration configuration = project.getConfigurations().getByName(configurationName);
            String configShortHand = Objects.requireNonNull(CONFIG_SHORTHAND.get(configurationName));
            for (Dependency d : configuration.getDependencies()) {
                String description = d.getGroup() + ":" + d.getName() + ":" + d.getVersion();
                seen.add(description);
                String excludes;
                if (d instanceof ModuleDependency md && !md.getExcludeRules().isEmpty()) {
                    excludes = "[-" + md.getExcludeRules().stream()
                            .map(er -> er.getGroup() + ":" + er.getModule())
                            .collect(Collectors.joining(";")) + "]";
                } else {
                    excludes = "";
                }
                dependencyList.add(description + ":" + configShortHand + excludes);
            }
        }
        // now the resolved path
        for (String configurationName : RESOLVABLE_CONFIGURATIONS) {
            Configuration configuration = project.getConfigurations().getByName(configurationName);
            String configShortHand = Objects.requireNonNull(CONFIG_SHORTHAND.get(configurationName));
            for (ResolvedArtifactResult rar : configuration.getIncoming().getArtifacts().getArtifacts()) {
                if (rar.getVariant().getOwner() instanceof ModuleComponentIdentifier mci) {
                    String description = mci.getGroup() + ":" + mci.getModule() + ":" + mci.getVersion();
                    if (seen.add(description)) {
                        dependencyList.add(description + ":" + configShortHand);
                    }
                }
            }
        }

        String dependencies = String.join(Main.COMMA, dependencyList);
        properties.put(DEPENDENCIES, dependencies);

        return !sourceDirectoriesPathSeparated.isEmpty() || !testDirectoriesPathSeparated.isEmpty();
    }

    private static String sourcePathFromSourceSet(SourceSet sourceSet) {
        return sourceSet.getAllJava().getSrcDirs()
                .stream()
                .filter(File::canRead)
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }

    private static String librariesFromSourceSet(SourceSet sourceSet) {
        return sourceSet.getCompileClasspath().getFiles()
                .stream()
                .filter(File::canRead)
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }

    private static String runtimeLibrariesFromSourceSet(SourceSet sourceSet) {
        return sourceSet.getRuntimeClasspath().getFiles()
                .stream()
                .filter(File::canRead)
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(File.pathSeparator));
    }

    private static void addSystemProperties(Map<String, Object> properties) {
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(PREFIX)) {
                LOGGER.debug("Overwriting property from system: {}", key);
                String strippedKey = key.substring(PREFIX.length());
                properties.put(strippedKey, entry.getValue().toString());
            }
        }
    }

    private static void flattenProperties(Map<String, Object> rawProperties,
                                          String projectPrefix,
                                          Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : rawProperties.entrySet()) {
            if (entry.getValue() != null) {
                String key = projectPrefix.isEmpty() ? entry.getKey() : (projectPrefix + "." + entry.getKey());
                properties.put(key, entry.getValue().toString());
            }
        }
    }
}
