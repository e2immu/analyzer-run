package org.e2immu.analyzer.run.main;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunComposer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunComposer.class);

    @Test
    public void test() {
        File file = new File("build/test-aapi-skeleton");
        if (file.delete()) {
            LOGGER.info("Deleting {}", file);
        }
        Main.main(new String[]{
                "--classpath=jmods/java.base.jmod",
                "--source=none",
                "--write-annotated-api-packages=java.util.",
                "--write-annotated-api-target-package=org.e2immu.aapi",
                "--write-annotated-api-dir=" + file.getAbsolutePath()
        });
        assertTrue(file.canRead());
    }
}
