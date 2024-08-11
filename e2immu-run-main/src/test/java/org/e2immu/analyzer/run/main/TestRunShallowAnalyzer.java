package org.e2immu.analyzer.run.main;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunShallowAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunShallowAnalyzer.class);

    @Test
    public void test() {
        File file = new File("build/test-shallow-analyzer");
        if (file.delete()) {
            LOGGER.info("Deleting {}", file);
        }
        Main.main(new String[]{
                "--classpath=jmods/java.base.jmod",
                "--source=../../analyzer-shallow/e2immu-shallow/aapi/src/main/java",
                "--read-annotated-api-packages=org.e2immu.analyzer.shallow.aapi.log",
                "--write-analyzed-annotated-api-dir=" + file.getAbsolutePath()
        });
        assertTrue(file.canRead());
    }
}
