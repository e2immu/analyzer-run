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


plugins {
    java
    `maven-publish`
}

group = "org.e2immu"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

repositories {
    maven {
        url = uri(project.findProperty("codeartifactUri") as String)
        credentials {
            username = "aws"
            password = project.findProperty("codeartifactToken") as String
        }
    }
    mavenCentral()
}

dependencies {
    implementation("org.e2immu:e2immu-external-support:${version}")
    implementation("org.e2immu:e2immu-internal-util:${version}")
    implementation("org.e2immu:e2immu-internal-graph:${version}")
    implementation("org.e2immu:e2immu-cst-api:${version}")
    implementation("org.e2immu:e2immu-cst-impl:${version}")
    implementation("org.e2immu:e2immu-inspection-api:${version}")
    implementation("org.e2immu:e2immu-inspection-resource:${version}")
    implementation("org.e2immu:e2immu-shallow-analyzer:${version}")
    implementation("org.e2immu:e2immu-modification-prepwork:${version}")
    implementation("org.e2immu:e2immu-run-config:${version}")
    implementation("org.e2immu:e2immu-run-main:${version}")

    implementation("org.apache.maven:maven-plugin-api:3.8.6")
    implementation("org.apache.maven:maven-core:3.8.6")
    implementation("org.apache.maven:maven-artifact:3.8.6")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:3.7.0")

    implementation("org.apache.maven.resolver:maven-resolver-api:1.8.2")
    implementation("org.apache.maven.resolver:maven-resolver-util:1.8.2")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("org.slf4j:slf4j-api:2.0.7")
}


publishing {
    repositories {
        maven {
            url = uri(project.findProperty("publishUri") as String)
            credentials {
                username = project.findProperty("publishUsername") as String
                password = project.findProperty("publishPassword") as String
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name = "analyzer-run-mvnplugin of e2immu analyser"
                description = "Static code analyser focusing on modification and immutability. " +
                        "This module provides a Maven plugin that writes the input configuration."
                url = "https://e2immu.org"
                scm {
                    url = "https://github.com/e2immu"
                }
                licenses {
                    license {
                        name = "GNU Lesser General Public License, version 3.0"
                        url = "https://www.gnu.org/licenses/lgpl-3.0.html"
                    }
                }
                developers {
                    developer {
                        id = "bnaudts"
                        name = "Bart Naudts"
                        email = "bart.naudts@e2immu.org"
                    }
                }
            }
        }
    }
}