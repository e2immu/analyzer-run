package org.e2immu.analyzer.run.main;

import ch.qos.logback.classic.Level;
import org.e2immu.language.inspection.integration.JavaInspectorImpl;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunAnalyzer.class);

    @BeforeAll
    public static void beforeAll() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.e2immu.analyzer.shallow")).setLevel(Level.DEBUG);
    }

    @Test
    public void test() {
        File sourceDir = new File("../../util-internal/e2immu-internal-util//src/main/java");
        assertTrue(sourceDir.isDirectory(), "Absolute = " + sourceDir.getAbsolutePath());

        Main.main(new String[]{
                "--debug=classpath",
                "--classpath=" + InputConfigurationImpl.DEFAULT_CLASSPATH_STRING,
                "--classpath=" + JavaInspectorImpl.JAR_WITH_PATH_PREFIX_DOUBLE_COLON + "org/slf4j",
                "--classpath=" + JavaInspectorImpl.JAR_WITH_PATH_PREFIX_DOUBLE_COLON + "ch/qos/logback/classic",
                "--classpath=" + JavaInspectorImpl.JAR_WITH_PATH_PREFIX_DOUBLE_COLON + "ch/qos/logback/core",
                "--source=" + sourceDir.getPath(),
                "--analysis-results-dir=build/e2immu",
                "--analyzed-annotated-api-dir=../../analyzer-shallow/e2immu-shallow-aapi/src/main/resources/json",
        });

    }
}
