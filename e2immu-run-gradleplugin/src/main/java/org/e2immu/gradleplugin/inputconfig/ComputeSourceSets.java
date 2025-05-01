package org.e2immu.gradleplugin.inputconfig;

import org.e2immu.language.cst.api.element.SourceSet;
import org.e2immu.language.inspection.resource.SourceSetImpl;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.internal.artifacts.DefaultProjectComponentIdentifier;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * targets for sources:
 * <ul>
 *     <li>multiple directories in a source set (DONE)</li>
 *     <li>source sets beyond main, test in the same project (e.g. functionalTest in testgradlepluginanalyzer (DONE)</li>
 *     <li>dependent source project in multi-project build</li>
 *     <li>dependent source projects in composite build (TODO, current attempts have failed)</li>
 * </ul>
 * <p>
 * target for classpath: simply the main flags: test, runtimeOnly, and filtering using "excludeFromClasspath".
 * There is no dependency information here.
 */
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
        LOGGER.info("Exit compute source sets with result: {} has source sets/classpath parts {}, {} dependent source projects",
                result.mainSourceSetName, result.sourceSetsByName.keySet(), result.sourceSetDependencies.size());
        return result;
    }

    private Result compute(Project project,
                           String restrictSourcesToPackages,
                           String restrictTestSourcesToPackages,
                           Set<String> excludeFromClasspath,
                           Set<String> projectsSeen) {
        projectsSeen.add(project.getName());
        LOGGER.info("Computing source sets of {}", project);

        String encoding = detectSourceEncoding(project);
        JavaPluginExtension javaPluginExtension = new DslObject(project).getExtensions()
                .getByType(JavaPluginExtension.class);
        Map<String, SourceSet> sourceSetsByName = new HashMap<>();
        String projectName = project.getName();
        for (org.gradle.api.tasks.SourceSet gradleSourceSet : javaPluginExtension.getSourceSets()) {
            String sourceSetName = projectName + "/" + gradleSourceSet.getName();
            boolean test = gradleSourceSet.getName().toLowerCase().contains("test");
            SourceSet sourceSet = makeSourceSet(gradleSourceSet, sourceSetName,
                    test ? restrictTestSourcesToPackages : restrictSourcesToPackages,
                    encoding, test);
            if (sourceSet != null) sourceSetsByName.put(sourceSet.name(), sourceSet);
        }
        List<Result> sourceSetDependencies = new ArrayList<>();
        List<Configuration> configurations = sortConfigurations(project);
        inspectConfigurations(project, excludeFromClasspath, projectsSeen, configurations, sourceSetsByName,
                sourceSetDependencies);
        String mainSourceSetName = projectName + "/main";
        return new Result(mainSourceSetName, sourceSetsByName, sourceSetDependencies);
    }

    private void inspectConfigurations(Project project, Set<String> excludeFromClasspath, Set<String> projectsSeen,
                                       List<Configuration> configurations, Map<String, SourceSet> sourceSetsByName,
                                       List<Result> sourceSetDependencies) {
        for (Configuration configuration : configurations) {
            if (configuration.isCanBeResolved()) {
                String configurationName = configuration.getName();
                LOGGER.info("Inspecting configuration {}", configurationName);
                boolean isTest = configurationName.toLowerCase().contains("test");
                boolean isRuntimeOnly = configurationName.toLowerCase().contains("runtime");

                for (ResolvedArtifactResult rar : configuration.getIncoming().getArtifacts().getArtifacts()) {
                    if (rar.getVariant().getOwner() instanceof ModuleComponentIdentifier mci) {
                        String description = mci.getGroup() + ":" + mci.getModule() + ":" + mci.getVersion();
                        if (!sourceSetsByName.containsKey(description)) {
                            LOGGER.info(" -- library dependency {} in {}", description, configurationName);
                            File file = rar.getFile();
                            if (file.canRead() && !excludeFromClasspath.contains(file.getName())
                                && !excludeFromClasspath.contains(description)
                                && !excludeFromClasspath.contains(mci.getModule())) {
                                SourceSet set = new SourceSetImpl(description,
                                        null, file.toURI(), null, isTest, true,
                                        true, false, isRuntimeOnly, null,
                                        null);
                                sourceSetsByName.put(description, set);
                            }
                        }
                    } else if (rar.getVariant().getOwner() instanceof DefaultProjectComponentIdentifier pci) {
                        String description = pci.getProjectName();
                        Project dependentProject = findProject(project, pci.getProjectName());
                        if (dependentProject != null && !dependentProject.equals(project)) {
                            if (!projectsSeen.contains(description)) {
                                LOGGER.info(" --  project dependency {} in configuration {}, looking for path {}", description,
                                        configurationName, pci.getProjectIdentity());
                                // recursion!!
                                Result result = compute(dependentProject, null,
                                        null, excludeFromClasspath, projectsSeen);
                                sourceSetDependencies.add(result);
                            }
                        } else {
                            LOGGER.info(" --  ignoring project dependency {} in configuration {}, not found",
                                    description, configurationName);
                        }
                    }
                }
            }
        }
    }

    private static @NotNull List<Configuration> sortConfigurations(Project project) {
        List<Configuration> configurations = new ArrayList<>(project.getConfigurations());
        configurations.sort((c1, c2) -> {
            String n1 = c1.getName();
            String n2 = c2.getName();
            boolean t1 = n1.toLowerCase().contains("runtime");
            boolean t2 = n2.toLowerCase().contains("runtime");
            if (!t1 && t2) return -1;
            if (t1 && !t2) return 1;
            boolean r1 = n1.toLowerCase().contains("test");
            boolean r2 = n1.toLowerCase().contains("test");
            if (!r1 && r2) return -1;
            if (r1 && !r2) return 1;
            return n1.compareTo(n2);
        });
        return configurations;
    }

    private Project findProject(Project project, String projectName) {
        Project local = project.getRootProject().getAllprojects().stream()
                .filter(p -> p.getName().equals(projectName)).findFirst().orElse(null);
        if (local != null) return local;
        //for (IncludedBuild ib : project.getGradle().getIncludedBuilds()) {
        //   LOGGER.info("included build: {} at {}", ib.getName(), ib.getProjectDir());
        //}
        return null;
    }


    private SourceSet makeSourceSet(org.gradle.api.tasks.SourceSet gradleSourceSet,
                                    String e2immuSourceSetName,
                                    String restrictTo,
                                    String encodingString,
                                    boolean test) {
        Set<String> restrictToPackages = restrictTo == null || restrictTo.isBlank() ? null :
                Arrays.stream(restrictTo.split("[,;]\\s*"))
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toUnmodifiableSet());
        Charset sourceEncoding = encodingString == null ? null : Charset.forName(encodingString);
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
