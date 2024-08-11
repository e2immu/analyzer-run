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

import org.gradle.api.Action;

public class AnalyzerExtension {
    public static final String ANALYZER_EXTENSION_NAME = "e2immu";
    public static final String ANALYZER_TASK_NAME = "e2immu-analyzer";

    public boolean skipProject;

    /* GeneralConfiguration */
    public boolean incrementalAnalysis;
    public boolean parallel;
    public String analysisSteps;
    public boolean quiet;
    public String debugTargets;

    /* InputConfiguration */
    public String jmods;
    public String jre;
    public String sourcePackages;
    public String testSourcePackages;

    /* from AnnotatedAPIConfiguration */
    // use case 1
    public String analyzedAnnotatedApiDirs;
    // use case 2
    public String writeAnalyzedAnnotatedAPIDir;
    // use case 3
    public String writeAnnotatedAPIDir;
    public String writeAnnotatedAPIPackages;
    public String writeAnnotatedAPITargetPackage;

    // actions
    public String action;
    public String[] actionParameters;

    private final ActionBroadcast<AnalyzerProperties> propertiesActions;

    public AnalyzerExtension(ActionBroadcast<AnalyzerProperties> propertiesActions) {
        this.propertiesActions = propertiesActions;
    }

    public void properties(Action<? super AnalyzerProperties> action) {
        propertiesActions.add(action);
    }
}
