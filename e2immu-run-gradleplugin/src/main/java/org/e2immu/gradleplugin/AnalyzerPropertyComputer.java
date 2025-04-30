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

import org.e2immu.analyzer.run.config.GeneralConfiguration;
import org.e2immu.analyzer.run.main.Main;
import org.e2immu.analyzer.shallow.analyzer.AnnotatedAPIConfiguration;
import org.e2immu.language.cst.api.runtime.LanguageConfiguration;
import org.e2immu.language.cst.impl.runtime.LanguageConfigurationImpl;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.e2immu.language.inspection.resource.SourceSetImpl;
import org.e2immu.util.internal.util.GradleConfiguration;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    public static final String DEPENDENCIES = "dependencies";
    public static final String E2IMMU_CONFIGURATION = "configuration.json";

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
        org.e2immu.analyzer.run.config.Configuration configuration = computeConfiguration(project, extension);
        rawProperties.put(E2IMMU_CONFIGURATION, configuration);

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

    private org.e2immu.analyzer.run.config.Configuration computeConfiguration(Project project, AnalyzerExtension extension) {
        LanguageConfiguration languageConfiguration = new LanguageConfigurationImpl(true);

        // general
        Map<String, String> generalMap = makeGeneralConfigMap(project, extension);
        GeneralConfiguration generalConfiguration = Main.generalConfiguration(generalMap);
        // Annotated API
        Map<String, String> aapiMap = makeAnnotatedAPIMap(extension);
        AnnotatedAPIConfiguration annotatedAPIConfiguration = Main.annotatedAPIConfiguration(aapiMap);
        // Input
        InputConfiguration inputConfiguration = makeInputConfiguration(project, extension);

        return new org.e2immu.analyzer.run.config.Configuration.Builder()
                .setAnnotatedAPIConfiguration(annotatedAPIConfiguration)
                .setGeneralConfiguration(generalConfiguration)
                .setLanguageConfiguration(languageConfiguration)
                .setInputConfiguration(inputConfiguration)
                .build();
    }

    private InputConfiguration makeInputConfiguration(Project project, AnalyzerExtension extension) {
        InputConfiguration.Builder builder = new InputConfigurationImpl.Builder();
        String encoding = detectSourceEncoding(project);
        builder.setAlternativeJREDirectory(extension.jre);

        JavaPluginExtension javaPluginExtension = new DslObject(project).getExtensions().getByType(JavaPluginExtension.class);
        makeSourceSet(javaPluginExtension, extension.sourcePackages, encoding, false)
                .forEach(builder::addSourceSets);
        makeSourceSet(javaPluginExtension, extension.testSourcePackages, encoding, true)
                .forEach(builder::addSourceSets);
        makeJavaModules(extension.jmods).forEach(builder::addClassPathParts);
        Map<String, org.e2immu.language.cst.api.element.SourceSet> classPathPartsByName = new HashMap<>();
        boolean[] both = {false, true};
        for (boolean test : both) {
            for (boolean runtimeOnly : both) {
                makeClassPathParts(javaPluginExtension, test, runtimeOnly, classPathPartsByName)
                        .forEach(builder::addClassPathParts);
            }
        }
        return builder.build();
    }

    private List<org.e2immu.language.cst.api.element.SourceSet> makeClassPathParts
            (JavaPluginExtension javaPluginExtension,
             boolean test,
             boolean runtimeOnly,
             Map<String, org.e2immu.language.cst.api.element.SourceSet> classPathPartsByName) {
        String sourceSetName = test ? "test" : "main";
        SourceSet gradleSet = javaPluginExtension.getSourceSets().getAt(sourceSetName);
        FileCollection files = runtimeOnly ? gradleSet.getRuntimeClasspath() : gradleSet.getCompileClasspath();
        List<org.e2immu.language.cst.api.element.SourceSet> sets = new ArrayList<>();
        for (File file : files) {
            String absolutePath = file.getAbsolutePath();
            if (file.canRead() && !classPathPartsByName.containsKey(absolutePath)) {
                org.e2immu.language.cst.api.element.SourceSet set = new SourceSetImpl(absolutePath, null,
                        file.toURI(), null, test, true, true, false,
                        runtimeOnly, null, null);
                classPathPartsByName.put(absolutePath, set);
                sets.add(set);
            }
        }
        return sets;
    }

    private List<org.e2immu.language.cst.api.element.SourceSet> makeJavaModules(String jmodsString) {
        if (jmodsString == null || jmodsString.isBlank()) return List.of();
        List<org.e2immu.language.cst.api.element.SourceSet> sets = new ArrayList<>();
        for (String jmod : jmodsString.split("[,;]\\s*")) {
            if (!jmod.isBlank()) {
                org.e2immu.language.cst.api.element.SourceSet set = new SourceSetImpl(jmod, null,
                        URI.create("jmod:" + jmod),
                        null, false, true, true, true, false,
                        null, null);
                sets.add(set);
            }
        }
        return sets;
    }

    private List<org.e2immu.language.cst.api.element.SourceSet> makeSourceSet(JavaPluginExtension javaPluginExtension,
                                                                              String restrictTo,
                                                                              String encodingString,
                                                                              boolean test) {
        Set<String> restrictToPackages = restrictTo == null || restrictTo.isBlank() ? null :
                Arrays.stream(restrictTo.split("[,;]\\s*"))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toUnmodifiableSet());
        Charset sourceEncoding = encodingString == null ? null : Charset.forName(encodingString);
        String sourceSetName = test ? "test" : "main";
        SourceSet gradleSet = javaPluginExtension.getSourceSets().getAt(sourceSetName);
        List<org.e2immu.language.cst.api.element.SourceSet> sets = new ArrayList<>();
        int cnt = 0;
        for (File file : gradleSet.getAllJava().getSrcDirs()) {
            if (file.canRead()) {
                Path path = file.toPath();
                String name = sourceSetName + (cnt == 0 ? "" : "" + cnt);
                org.e2immu.language.cst.api.element.SourceSet set = new SourceSetImpl(name, path, path.toUri(),
                        sourceEncoding, test, false, false, false, false,
                        restrictToPackages, null);
                sets.add(set);
                ++cnt;
            }
        }
        return sets;
    }

    private static Map<String, String> makeAnnotatedAPIMap(AnalyzerExtension extension) {
        // Annotated API
        // use case 1
        Map<String, String> kvMap = new HashMap<>();
        if (extension.analyzedAnnotatedApiDirs != null) {
            kvMap.put(Main.ANALYZED_ANNOTATED_API_DIRS, extension.analyzedAnnotatedApiDirs);
        }
        // use case 2
        if (extension.analyzedAnnotatedApiTargetDir != null) {
            kvMap.put(Main.ANALYZED_ANNOTATED_API_TARGET_DIR, extension.analyzedAnnotatedApiTargetDir);
        }
        // use case 3
        if (extension.annotatedApiTargetDir != null) {
            kvMap.put(Main.ANNOTATED_API_TARGET_DIR, extension.annotatedApiTargetDir);
        }
        if (extension.annotatedApiTargetPackage != null) {
            kvMap.put(Main.ANNOTATED_API_TARGET_PACKAGE, extension.annotatedApiTargetPackage);
        }
        if (extension.annotatedApiPackages != null) {
            kvMap.put(Main.ANNOTATED_API_PACKAGES, extension.annotatedApiPackages);
        }
        return kvMap;
    }

    private static @NotNull Map<String, String> makeGeneralConfigMap(Project project,
                                                                     AnalyzerExtension extension) {
        Map<String, String> generalMap = new HashMap<>();
        generalMap.put(Main.INCREMENTAL_ANALYSIS, "" + extension.incrementalAnalysis);
        String analysisResultsDir;
        if (extension.analysisResultsDir != null) {
            analysisResultsDir = extension.analysisResultsDir;
        } else {
            // default value: "${build.dir}/e2immu"
            File buildDir = project.getLayout().getBuildDirectory().get().getAsFile();
            analysisResultsDir = new File(buildDir, "e2immu").getAbsolutePath();
        }
        generalMap.put(Main.ANALYSIS_RESULTS_DIR, analysisResultsDir);
        generalMap.put(Main.PARALLEL, "" + extension.parallel);
        generalMap.put(Main.ANALYSIS_STEPS, extension.analysisSteps);
        generalMap.put(Main.DEBUG, extension.debugTargets);
        generalMap.put(Main.QUIET, "" + extension.quiet);
        return generalMap;
    }

    private static String getOrDefault(String property, String defaultValue) {
        return property == null || property.isBlank() ? defaultValue : property;
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
