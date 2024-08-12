package org.e2immu.analyzer.run.main;

import org.e2immu.analyzer.modification.prepwork.hct.ComputeHiddenContent;
import org.e2immu.analyzer.modification.prepwork.hct.HiddenContentTypes;
import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.shallow.analyzer.*;
import org.e2immu.language.cst.api.info.Info;
import org.e2immu.language.cst.api.info.TypeInfo;
import org.e2immu.language.cst.impl.analysis.PropertyImpl;
import org.e2immu.language.inspection.api.integration.JavaInspector;
import org.e2immu.language.inspection.api.parser.Summary;
import org.e2immu.language.inspection.integration.JavaInspectorImpl;
import org.e2immu.util.internal.util.Trie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
        try {
            AnnotatedAPIConfiguration ac = configuration.annotatedAPIConfiguration();
            if (ac.annotatedApiTargetDir() != null) {
                runComposer();
                return;
            }
            if (ac.analyzedAnnotatedApiTargetDir() != null) {
                runShallowAnalyzer();
                return;
            }
            runAnalyzer();
        } catch (IOException ioe) {
            LOGGER.error("Caught IO exception: {}", ioe.getMessage());
            exitValue = 1;
        }
    }

    private void runAnalyzer() throws IOException {
        JavaInspector javaInspector = new JavaInspectorImpl();
        javaInspector.initialize(configuration.inputConfiguration());

        AnnotatedAPIConfiguration ac = configuration.annotatedAPIConfiguration();
        for (String dir : ac.analyzedAnnotatedApiDirs()) {
            File directory = new File(dir);
            if (directory.canRead()) {
                new Load().go(javaInspector, directory);
                LOGGER.info("Read json files in AAAPI {}", directory.getAbsolutePath());
            } else {
                LOGGER.warn("Path '{}' is not a directory containing analyzed annotated API files", directory);
            }
        }

        Summary summary = javaInspector.parse(false);
        if (summary.haveErrors()) {
            LOGGER.error("Have parsing errors, bailing out");
            return;
        }
        List<String> analysisSteps = configuration.generalConfiguration().analysisSteps();
        if (analysisSteps.size() == 1 && "none".equalsIgnoreCase(analysisSteps.get(0))) {
            return;
        }
        boolean empty = analysisSteps.isEmpty();
        if (empty || analysisSteps.contains("hc")) {
            LOGGER.info("Computing hidden content for {} types", summary.types().size());
            ComputeHiddenContent chc = new ComputeHiddenContent(javaInspector.runtime());
            for (TypeInfo typeInfo : summary.types()) {
                typeInfo.recursiveSubTypeStream().forEach(st -> {
                    HiddenContentTypes stHct = chc.compute(st);
                    st.analysis().set(HiddenContentTypes.HIDDEN_CONTENT_TYPES, stHct);
                    st.methodAndConstructorStream().forEach(m -> {
                        HiddenContentTypes mHct = chc.compute(stHct, m);
                        m.analysis().set(HiddenContentTypes.HIDDEN_CONTENT_TYPES, mHct);
                    });
                });
            }
        }

        // write results
        String targetDir = configuration.generalConfiguration().analysisResultsDir();
        if (targetDir != null && !"none".equalsIgnoreCase(targetDir)) {
            Trie<TypeInfo> trie = new Trie<>();
            LOGGER.info("Writing results for {} types to {}", summary.types().size(), targetDir);
            summary.types().forEach(ti -> trie.add(ti.packageName().split("\\."), ti));
            WriteAnalysis writeAnalysis = new WriteAnalysis();
            writeAnalysis.write(targetDir, trie);
        } else {
            LOGGER.warn("Not writing out results, " + Main.ANALYSIS_RESULTS_DIR + " is empty");
        }
    }

    private void runShallowAnalyzer() throws IOException {
        AnnotatedAPIConfiguration ac = configuration.annotatedAPIConfiguration();
        AnnotatedApiParser annotatedApiParser = new AnnotatedApiParser();

        annotatedApiParser.initialize(configuration.inputConfiguration(), ac);
        LOGGER.info("AAPI parser finds {} types", annotatedApiParser.types().size());
        ShallowAnalyzer shallowAnalyzer = new ShallowAnalyzer(annotatedApiParser);
        Trie<TypeInfo> trie = new Trie<>();
        List<TypeInfo> types = shallowAnalyzer.go();
        LOGGER.info("Shallow analyzer found {} types", types.size());
        annotatedApiParser.types().forEach(ti -> trie.add(ti.packageName().split("\\."), ti));
        WriteAnalysis writeAnalysis = new WriteAnalysis();
        writeAnalysis.write(ac.analyzedAnnotatedApiTargetDir(), trie);

        LOGGER.info("End of e2immu main, AAPI->AAAPI shallow analyzer.");
    }

    private void runComposer() throws IOException {
        JavaInspector javaInspector = new JavaInspectorImpl();

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
        composer.write(apiTypes, ac.annotatedApiTargetDir());

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
