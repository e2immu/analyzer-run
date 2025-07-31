package org.e2immu.analyzer.run.main;

import org.e2immu.language.cst.api.info.Info;
import org.e2immu.language.cst.api.info.TypeInfo;
import org.e2immu.language.inspection.api.integration.JavaInspector;
import org.e2immu.language.inspection.api.parser.ParseResult;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.language.inspection.integration.JavaInspectorImpl;
import org.e2immu.util.internal.graph.G;
import org.e2immu.util.internal.graph.V;
import org.e2immu.util.internal.graph.op.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.e2immu.language.inspection.api.integration.JavaInspector.InvalidationState.*;

public class RunRewireTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunRewireTests.class);
    private static final int ITERATIONS = 3;

    private final InputConfiguration inputConfiguration;
    private final JavaInspector javaInspector;
    private final ParseResult parseResult;
    private final G<TypeInfo> primaryTypeUseGraph;

    public RunRewireTests(InputConfiguration inputConfiguration,
                          JavaInspector javaInspector,
                          ParseResult parseResult,
                          G<Info> graph) {
        this.inputConfiguration = inputConfiguration;
        this.javaInspector = javaInspector;
        this.parseResult = parseResult;
        G.Builder<TypeInfo> typeUseBuilder = new G.Builder<>(Long::sum);
        graph.edgeStream().forEach(e -> {
            TypeInfo ptFrom = e.from().t().typeInfo().primaryType();
            TypeInfo ptTo = e.to().t().typeInfo().primaryType();
            if (ptFrom != ptTo) typeUseBuilder.mergeEdge(ptTo, ptFrom, 1L);
        });
        primaryTypeUseGraph = typeUseBuilder.build();
    }

    public void go() {
        Random random = new Random(12L);
        List<TypeInfo> list = parseResult.primaryTypes().stream().sorted(Comparator.comparing(TypeInfo::fullyQualifiedName)).toList();
        for (int i = 0; i < ITERATIONS; ++i) {
            TypeInfo pt = list.get(random.nextInt(list.size()));
            Path path = Path.of(pt.compilationUnit().uri().getSchemeSpecificPart());
            Path absolutePath = path.toAbsolutePath();
            LOGGER.info("Modifying {}", absolutePath);
            boolean stop = false;
            try {
                String content = Files.readString(path);
                try {
                    String updatedContent = "// some comment\n\n" + content;
                    // this will trigger a change of fingerprint
                    stop |= write(path, updatedContent);
                    if (!stop) {
                        LOGGER.info("Calling javaInspector.reloadSources");
                        JavaInspector.ReloadResult rr = javaInspector.reloadSources(inputConfiguration, Map.of());
                        assert rr.sourceHasChanged().size() == 1;
                        TypeInfo sourceHasChanged = rr.sourceHasChanged().stream().findFirst().orElseThrow();
                        assert sourceHasChanged.equals(pt);
                        Set<TypeInfo> dependentPrimaryTypes = dependent(pt);
                        LOGGER.info("{} has {} dependent types", pt, dependentPrimaryTypes.size());
                        JavaInspector.Invalidated invalidated = ti -> {
                            if (ti == pt) return INVALID;
                            if (dependentPrimaryTypes.contains(ti)) return REWIRE;
                            return UNCHANGED;
                        };
                        JavaInspector.ParseOptions parseOptions = new JavaInspectorImpl.ParseOptionsBuilder()
                                .setDetailedSources(true)
                                .setFailFast(true)
                                .setInvalidated(invalidated)
                                .setParallel(true)
                                .build();
                        javaInspector.parse(parseOptions);
                    }
                } finally {
                    LOGGER.info("Restoring {}", absolutePath);
                    stop |= write(path, content);
                }
            } catch (IOException ioe) {
                LOGGER.error("Could not read {}", absolutePath, ioe);
                stop = true;
            }
            if (stop) return;
        }
    }

    private Set<TypeInfo> dependent(TypeInfo pt) {
        return Common.follow(primaryTypeUseGraph, new V<>(pt)).stream().map(V::t).collect(Collectors.toUnmodifiableSet());
    }

    private static boolean write(Path path, String content) {
        try {
            Files.writeString(path, content);
            return false;
        } catch (IOException ioe) {
            LOGGER.error("Could not write {}", path.toAbsolutePath(), ioe);
            return true;
        }
    }
}
