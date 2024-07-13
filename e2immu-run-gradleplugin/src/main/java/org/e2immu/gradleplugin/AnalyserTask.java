/*
 * e2immu: a static code analyser for effective and eventual immutability
 * Copyright 2020-2021, Bart Naudts, https://www.e2immu.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.e2immu.gradleplugin;


import org.e2immu.analyzer.run.config.Configuration;
import org.e2immu.analyzer.run.main.ExecuteAction;
import org.e2immu.analyzer.run.main.Main;
import org.e2immu.analyzer.run.main.RunAnalyzer;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.util.LinkedHashMap;
import java.util.Map;


public class AnalyserTask extends ConventionTask {
    private static final Logger LOGGER = Logging.getLogger(AnalyserTask.class);
    private Map<String, String> analyserProperties;

    @TaskAction
    public void run() {
        Map<String, String> properties = getProperties();

        if (properties.isEmpty()) {
            LOGGER.warn("Skipping e2immu analysis: no properties configured, was it skipped in all projects?");
            return;
        }

     if (!LOGGER.isInfoEnabled()) {
            properties.put(Main.QUIET, "true");
        } else if (LOGGER.isDebugEnabled()) {
            String inExtension = properties.get(Main.DEBUG);
            if (inExtension == null || inExtension.trim().isEmpty()) {
                properties.put(Main.DEBUG, inExtension);
            }
        }
        Configuration configuration = Main.fromPropertyMap(properties);
        String action = properties.get(Main.ACTION);
        if (action != null) {
            String ap = properties.get(Main.ACTION_PARAMETER);
            String[] actionParameters = ap == null ? new String[0] : ap.split(AnalyserPropertyComputer.M_A_G_I_C);
            int exitValue = ExecuteAction.run(action, actionParameters, configuration);
            if (exitValue != 0) {
                throw new RuntimeException("Analyser exited with error value " + exitValue + ": "
                                           + Main.exitMessage(exitValue));
            }
            return;
        }
        LOGGER.debug("Configuration:\n{}", configuration);

        RunAnalyzer runAnalyser = new RunAnalyzer(configuration);
        runAnalyser.run();
        // print to standard out = QUIET level
        runAnalyser.printSummaries();
        int exitValue = runAnalyser.exitValue();
        if (exitValue != 0) {
            throw new RuntimeException("Analyser exited with error value " + exitValue + ": " + Main.exitMessage(exitValue));
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
