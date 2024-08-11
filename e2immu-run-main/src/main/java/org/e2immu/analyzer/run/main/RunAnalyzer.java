package org.e2immu.analyzer.run.main;

import ch.qos.logback.classic.Level;
import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.shallow.analyzer.*;
import org.e2immu.language.cst.api.info.Info;
import org.e2immu.language.cst.api.info.TypeInfo;
import org.e2immu.language.inspection.api.integration.JavaInspector;
import org.e2immu.language.inspection.api.parser.Summary;
import org.e2immu.language.inspection.api.resource.InputConfiguration;
import org.e2immu.language.inspection.integration.JavaInspectorImpl;
import org.e2immu.util.internal.util.Trie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
        if (ac.annotatedApiTargetDirectory() != null) {
            runComposer();
            return;
        }
        if (ac.analyzedAnnotatedApiTargetDirectory() != null) {
            runShallowAnalyzer();
            return;
        }
        // start parsing, then run analyzer
    }

    private void runShallowAnalyzer() {
        AnnotatedAPIConfiguration ac = configuration.annotatedAPIConfiguration();
        AnnotatedApiParser annotatedApiParser = new AnnotatedApiParser();
        try {
            annotatedApiParser.initialize(configuration.inputConfiguration(), ac);
            LOGGER.info("AAPI parser finds {} types", annotatedApiParser.types().size());
            ShallowAnalyzer shallowAnalyzer = new ShallowAnalyzer(annotatedApiParser);
            Trie<TypeInfo> trie = new Trie<>();
            List<TypeInfo> types = shallowAnalyzer.go();
            LOGGER.info("Shallow analyzer found {} types", types.size());
            annotatedApiParser.types().forEach(ti -> trie.add(ti.packageName().split("\\."), ti));
            WriteAnalysis writeAnalysis = new WriteAnalysis();
            writeAnalysis.write(ac.analyzedAnnotatedApiTargetDirectory(), trie);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            LOGGER.error("Caught IO exception: {}", ioe.getMessage());
            exitValue = 1;
        }
        LOGGER.info("End of e2immu main, AAPI->AAAPI shallow analyzer.");
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
                LOGGER.info("No filter.");
            } else {
                filter = new PackageFilter(ac.annotatedApiPackages());
                LOGGER.info("Created package filter based on {}", ac.annotatedApiPackages());
            }
            Composer composer = new Composer(javaInspector.runtime(), destinationPackage, filter);
            List<TypeInfo> compiledPrimaryTypes = javaInspector.compiledTypesManager()
                    .typesLoaded().stream().filter(TypeInfo::isPrimaryType).toList();
            LOGGER.info("Loaded {} compiled primary types", compiledPrimaryTypes.size());

            Summary summary = javaInspector.parse(true);
            Collection<TypeInfo> sourcePrimaryTypes = summary.types();
            LOGGER.info("Parsed {} primary source types", sourcePrimaryTypes.size());

            List<TypeInfo> primaryTypes = Stream.concat(compiledPrimaryTypes.stream(), sourcePrimaryTypes.stream()).toList();
            Collection<TypeInfo> apiTypes = composer.compose(primaryTypes);
            composer.write(apiTypes, ac.annotatedApiTargetDirectory());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            LOGGER.error("Caught IO exception: {}", ioe.getMessage());
            exitValue = 1;
        }
        LOGGER.info("End of e2immu main, AAPI skeleton generation mode.");
    }

    record PackageFilter(List<String> acceptedPackages) implements Predicate<Info> {

        @Override
        public boolean test(Info info) {
            if (acceptedPackages.isEmpty()) {
                return true;
            }
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
