package org.e2immu.gradleplugin.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.run.config.util.JsonStreaming;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.language.inspection.resource.InputConfigurationImpl;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.e2immu.gradleplugin.AnalyzerPropertyComputer.E2IMMU_CONFIGURATION;

public class WriteInputConfigurationTask extends ConventionTask {
    private static final Logger LOGGER = Logging.getLogger(AnalyzerTask.class);
    private Map<String, String> analyserProperties;

    @TaskAction
    public void run() {
        Map<String, String> properties = getProperties();
        String configurationJson = properties.get(E2IMMU_CONFIGURATION);
        ObjectMapper objectMapper = JsonStreaming.objectMapper();
        try {
            Configuration configuration = objectMapper.readerFor(Configuration.class)
                    .readValue(configurationJson);
            InputConfiguration inputConfiguration = configuration.inputConfiguration();
            File buildDir = getProject().getLayout().getBuildDirectory().getAsFile().get();
            File outFile = new File(buildDir, "inputConfiguration.json");
            objectMapper.writerFor(InputConfigurationImpl.class).writeValue(outFile, inputConfiguration);
        } catch (IOException ioException) {
            LOGGER.error("Caught IOException", ioException);
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
