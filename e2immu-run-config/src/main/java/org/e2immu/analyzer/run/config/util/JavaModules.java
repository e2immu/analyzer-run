package org.e2immu.analyzer.run.config.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JavaModules {

    public static Set<String> jmodsFromString(String jmodsString) {
        Set<String> jmods = new HashSet<>();
        Collections.addAll(jmods, "java.base");
        if (jmodsString != null && !jmodsString.isBlank()) {
            String[] split = jmodsString.split("[,;]\\s*");
            Collections.addAll(jmods, split);
            boolean change = true;
            while (change) {
                change = false;
                Set<String> copy = new HashSet<>(jmods);
                for (String jmod : copy) {
                    for (String dep : jmodDependency(jmod)) {
                        if (jmods.add(dep)) change = true;
                    }
                }
            }
        }
        return jmods;
    }

    /*
    How to find the dependencies of jdk.* modules?

    jar xf /opt/homebrew/Cellar/openjdk/23.0.2/libexec/openjdk.jdk/Contents/Home/jmods/jdk.unsupported.jmod classes/module-info.class
    javap classes/module-info.class
     */
    public static Set<String> jmodDependency(String jmod) {
        return switch (jmod) {
            case "java.base" -> Set.of();
            case "java.desktop" -> Set.of("java.xml", "java.datatransfer");
            case "java.management.rmi" -> Set.of("java.management", "java.rmi");
            case "java.se" -> Set.of("java.scripting", "java.sql.rowset", "java.xml.crypto", "java.desktop",
                    "java.compiler", "java.instrument", "java.management.rmi", "java.net.http", "java.prefs",
                    "java.security.jgss", "java.security.sasl");
            case "java.sql" -> Set.of("java.logging", "java.xml", "java.transaction.xa");
            case "java.sql.rowset" -> Set.of("java.sql", "java.naming");
            case "java.xml.crypto" -> Set.of("java.xml");
            case "java.compiler", "java.datatransfer", "java.instrument",
                 "java.logging", "java.management", "java.naming", "java.net.http", "java.prefs", "java.rmi",
                 "java.scripting", "java.security.jgss", "java.security.sasl", "java.smartcardio",
                 "java.transaction.xa", "java.xml",
                 "jdk.unsupported" -> Set.of("java.base");
            default -> throw new UnsupportedOperationException("Implement: " + jmod);
        };
    }
}
