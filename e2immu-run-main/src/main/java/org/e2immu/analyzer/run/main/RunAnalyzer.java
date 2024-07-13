package org.e2immu.analyzer.run.main;

import org.e2immu.analyzer.run.config.Configuration;

public class RunAnalyzer implements Runnable {

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

    }

    public void printSummaries() {

    }
}
