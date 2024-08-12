package org.e2immu.analyzer.run.main;

import ch.qos.logback.classic.Level;
import org.e2immu.language.inspection.integration.JavaInspectorImpl;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunShallowAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunShallowAnalyzer.class);

    @BeforeAll
    public static void beforeAll() {
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);
        ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.e2immu.analyzer.shallow")).setLevel(Level.DEBUG);
    }

    @Test
    public void test() {
        File aapiSources = new File("../../analyzer-shallow/e2immu-shallow-aapi/src/main/java");
        assertTrue(aapiSources.isDirectory(), "Absolute = " + aapiSources.getAbsolutePath());

        File file = new File("build/test-shallow-analyzer");
        if (file.isDirectory()) {
            //noinspection ALL
            Arrays.stream(file.listFiles()).forEach(File::delete);
        }
        if (file.mkdirs()) {
            LOGGER.info("Created {}", file);
        }
        Main.main(new String[]{
                "--debug=classpath",
                "--classpath=" + InputConfigurationImpl.DEFAULT_CLASSPATH_STRING,
                "--classpath=" + JavaInspectorImpl.JAR_WITH_PATH_PREFIX_DOUBLE_COLON + "org/slf4j",
                "--classpath=" + JavaInspectorImpl.JAR_WITH_PATH_PREFIX_DOUBLE_COLON + "ch/qos/logback/classic",
                "--classpath=" + JavaInspectorImpl.JAR_WITH_PATH_PREFIX_DOUBLE_COLON + "ch/qos/logback/core",
                "--source=" + aapiSources.getPath(),
                "--source-packages=org.e2immu.analyzer.shallow.aapi.log",
                "--analyzed-annotated-api-dirs=src/test/resources/json",
                "--analyzed-annotated-api-target-dir=" + file.getAbsolutePath()
        });
        assertTrue(file.canRead());

        File orgSlf4j = new File(file, "OrgSlf4j.json");
        assertTrue(orgSlf4j.canRead());
        File chQosLogbackClassic = new File(file, "ChQosLogbackClassic.json");
        assertTrue(chQosLogbackClassic.canRead());

        File javaLang = new File(file, "JavaLang.json");
        assertFalse(javaLang.canRead());
        File javaIo = new File(file, "JavaIo.json");
        assertFalse(javaIo.canRead());
    }
}
