package org.e2immu.gradleplugin.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.run.main.Main;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

public class WriteInputConfigurationTask extends ConventionTask {
    private static final Logger LOGGER = Logging.getLogger(AnalyzerTask.class);
    private Map<String, String> analyserProperties;

    @TaskAction
    public void run() {
        Map<String, String> properties = getProperties();

        Configuration configuration = Main.fromPropertyMap(properties);
        File outFile = new File("inputConfiguration.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String s = objectMapper.writerFor(InputConfigurationImpl.class).withDefaultPrettyPrinter()
                    .writeValueAsString(configuration.inputConfiguration());
            Files.writeString(outFile.toPath(), s);
        } catch (IOException ioException) {
            LOGGER.error("Failed to write to {}", outFile, ioException);
        }
    }

    /**
     * @return The String key/value pairs to be passed to the analyser.
     * {@code null} values are not permitted.
     */
    @Input
    public Map<String, String> getProperties() {
        if (analyserProperties == null) {
            analyserProperties = new LinkedHashMap<>();
        }
        return analyserProperties;
    }

}
