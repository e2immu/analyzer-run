package org.e2immu.analyzer.run.main;

import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.shallow.analyzer.AnnotatedAPIConfiguration;
import org.e2immu.analyzer.shallow.analyzer.Composer;
import org.e2immu.language.cst.api.info.Info;
import org.e2immu.language.cst.api.info.TypeInfo;
import org.e2immu.language.inspection.api.integration.JavaInspector;
import org.e2immu.language.inspection.integration.JavaInspectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RunAnalyzer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunAnalyzer.class);

    private final Configuration configuration;
    private int exitValue;

    public RunAnalyzer(Configuration configuration) {
        this.configuration = configuration;
    }

    public int exitValue() {
        return exitValue;
    }

    @Override
    public void run() {
        AnnotatedAPIConfiguration ac = configuration.annotatedAPIConfiguration();
        if (ac.annotatedApiTargetDirectory() != null && !ac.annotatedApiPackages().isEmpty()) {
            runComposer();
            return;
        }
        if (ac.analyzedAnnotatedApiTargetDirectory() != null && !ac.annotatedApiSourcePackages().isEmpty()) {
            runShallowAnalyzer(ac);
            return;
        }

    }

    private void runShallowAnalyzer(AnnotatedAPIConfiguration ac) {
    }

    private void runComposer() {
        JavaInspector javaInspector = new JavaInspectorImpl();
        try {
            javaInspector.initialize(configuration.inputConfiguration());

            AnnotatedAPIConfiguration ac = configuration.annotatedAPIConfiguration();
            String destinationPackage = ac.annotatedApiTargetPackage() == null ? "" : ac.annotatedApiTargetPackage();
            Predicate<Info> filter;
            if (ac.annotatedApiPackages().isEmpty()) {
                filter = w -> true;
            } else {
                filter = new PackageFilter(ac.annotatedApiPackages());
            }
            Composer composer = new Composer(javaInspector.runtime(), destinationPackage, filter);
            List<TypeInfo> primaryTypes = javaInspector.compiledTypesManager()
                    .typesLoaded().stream().filter(TypeInfo::isPrimaryType).toList();
            LOGGER.info("Have {} primary types loaded", primaryTypes.size());
            Collection<TypeInfo> apiTypes = composer.compose(primaryTypes);

            Path destination = Path.of(ac.annotatedApiTargetDirectory());

            if (destination.toFile().mkdirs()) {
                LOGGER.info("Created directory {}", destination);
            }
            composer.write(apiTypes, ac.annotatedApiTargetDirectory());
        } catch (IOException ioe) {
            LOGGER.error("Caught IO exception: {}", ioe.getMessage());
            exitValue = 1;
        }
        LOGGER.info("End of e2immu main, AAPI skeleton generation mode.");
    }

    record PackageFilter(List<String> acceptedPackages) implements Predicate<Info> {

        @Override
        public boolean test(Info info) {
            String myPackageName = info.typeInfo().packageName();
            for (String s : acceptedPackages) {
                if (s.endsWith(".")) {
                    if (myPackageName.startsWith(s)) return true;
                    String withoutDot = s.substring(0, s.length() - 1);
                    if (myPackageName.equals(withoutDot)) return true;
                } else if (myPackageName.equals(s)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void printSummaries() {

    }
}
