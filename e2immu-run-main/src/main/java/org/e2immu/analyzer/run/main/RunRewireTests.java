package org.e2immu.analyzer.run.main;

import org.e2immu.language.cst.api.info.Info;
import org.e2immu.language.cst.api.info.TypeInfo;
import org.e2immu.language.inspection.api.integration.JavaInspector;
import org.e2immu.language.inspection.api.parser.ParseResult;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.util.internal.graph.G;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;

public record RunRewireTests(InputConfiguration inputConfiguration,
                             JavaInspector javaInspector,
                             ParseResult parseResult,
                             G<Info> graph) {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunRewireTests.class);
    private static final int ITERATIONS = 1;

    public void go() {
        Random random = new Random(12L);
        int n = parseResult.primaryTypes().size();
        for (int i = 0; i < ITERATIONS; ++i) {
            TypeInfo pt = parseResult.primaryTypes().stream().toList().get(random.nextInt(n));
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
