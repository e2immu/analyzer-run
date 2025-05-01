package org.e2immu.gradleplugin.inputconfig;

import org.e2immu.language.cst.api.element.SourceSet;
import org.e2immu.util.internal.graph.G;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComputeDependencies {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeDependencies.class);

    public G<String> go(ComputeSourceSets.Result result) {
        G.Builder<String> builder = new G.Builder<>(Long::sum);

        // jmods are common
        Set<String> jmods = new HashSet<>();
        for (SourceSet sourceSet : result.sourceSetsByName().values()) {
            if (sourceSet.partOfJdk()) {
                String jmod = sourceSet.name();
                Set<String> dependencies = jmodDependency(jmod);
                LOGGER.info("Adding JMOD {} -> {}", jmod, dependencies);
                builder.add(jmod, dependencies);
                jmods.add(jmod);
            }
        }
        Set<String> jmodsAndExternal = new HashSet<>(jmods);
        HashSet<String> seen = new HashSet<>();
        recursionForClassPathParts(builder, result, seen, jmods, jmodsAndExternal);

        recursionForSourceSets(builder, result, seen, jmodsAndExternal);
        return builder.build();
    }

    private void recursionForClassPathParts(G.Builder<String> builder, ComputeSourceSets.Result result,
                                            Set<String> seen, Set<String> jmods, Set<String> jmodsAndExternal) {

        // depth first
        for (ComputeSourceSets.Result sub : result.sourceSetDependencies()) {
            recursionForClassPathParts(builder, sub, seen, jmods, jmodsAndExternal);
        }

        // every external library is dependent on all the jmods
        for (SourceSet sourceSet : result.sourceSetsByName().values()) {
            String name = sourceSet.name();
            if (sourceSet.externalLibrary() && seen.add(name)) {
                builder.add(name, jmods);
                LOGGER.info("Adding EXT {} -> {}", name, jmods);
                jmodsAndExternal.add(name);
            }
        }
    }

    private List<String> recursionForSourceSets(G.Builder<String> builder, ComputeSourceSets.Result result,
                                                Set<String> seen, Set<String> jmodsAndExternal) {
        if (!seen.add(result.mainSourceSetName())) return List.of();

        // depth first
        List<String> dependentSourceSets = new ArrayList<>();
        for (ComputeSourceSets.Result sub : result.sourceSetDependencies()) {
            dependentSourceSets.addAll(recursionForSourceSets(builder, sub, seen, jmodsAndExternal));
        }

        List<String> mainSourceSets = new ArrayList<>();
        List<String> testSourceSets = new ArrayList<>();

        // every source set is dependent on all the external libraries, and the jmods
        for (SourceSet sourceSet : result.sourceSetsByName().values()) {
            if (!sourceSet.externalLibrary()) {
                String name = sourceSet.name();
                LOGGER.info("Adding SRC->EXT/JMOD {} -> {}", name, jmodsAndExternal);
                builder.add(name, jmodsAndExternal);
                LOGGER.info("Adding SRC->DEP {} -> {}", name, dependentSourceSets);
                builder.add(name, dependentSourceSets);

                if(sourceSet.test()) {
                    testSourceSets.add(name);
                } else {
                    mainSourceSets.add(name);
                }
            }
        }
        for(String testName: testSourceSets) {
            LOGGER.info("ADDING SRC MAIN->TEST {} -> {}", testName, mainSourceSets);
            builder.add(testName, mainSourceSets);
        }
        return mainSourceSets;
    }

    private static Set<String> jmodDependency(String jmod) {
        return switch (jmod) {
            case "java.base" -> Set.of();
            case "java.desktop" -> Set.of("java.xml", "java.datatransfer");
            case "java.management.rmi" -> Set.of("java.management", "java.rmi");
            case "java.se" -> Set.of("java.scripting", "java.sql.rowset", "java.xml.crypto", "java.desktop",
                    "java.compiler", "java.instrument", "java.management.rmi", "java.net.http", "java.prefs",
                    "java.security.jgss", "java.security.sasl");
            case "java.sql" -> Set.of("java.logging", "java.xml", "java.transaction.xa");
            case "java.sql.rowset" -> Set.of("java.sql", "java.naming");
            case "java.xml.crypto" -> Set.of("java.xml");
            case "java.compiler", "java.datatransfer", "java.instrument",
                 "java.logging", "java.management", "java.naming", "java.net.http", "java.prefs", "java.rmi",
                 "java.scripting", "java.security.jgss", "java.security.sasl", "java.smartcardio",
                 "java.transaction.xa", "java.xml" -> Set.of("java.base");
            default -> throw new UnsupportedOperationException("Implement: " + jmod);
        };
    }
}
