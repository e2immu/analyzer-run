package org.e2immu.analyzer.run.testgradleplugwriteaapi;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRunShallow {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunShallow.class);

    @Test
    public void test() throws IOException {
        LOGGER.info("In test!");
        File dir = new File("/tmp/testWriteAnalyzedAnnotatedAPIDir");
        assertTrue(dir.canRead());

        File juc = new File(dir, "JavaUtilConcurrent.json");
        assertTrue(juc.canRead());

        File ju = new File(dir, "JavaUtil.json");
        assertFalse(ju.canRead());

        File juf = new File(dir, "JavaUtilFunction.json");
        assertTrue(juf.canRead());

        String jufContent = Files.readString(juf.toPath());
        assertTrue(jufContent.contains("Tjava.util.function.BiConsumer"));
    }
}
