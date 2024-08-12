plugins {
    java
    id("org.e2immu.analyzer-plugin")
}

group = "io.codelaser.build"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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


// see https://docs.gradle.org/current/userguide/testing_gradle_plugins.html
val functionalTest: SourceSet by sourceSets.creating

dependencies {
    implementation(gradleApi())
    implementation("org.e2immu:e2immu-external-support:some.version")
    implementation("org.e2immu:e2immu-internal-util:some.version")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3") // used in tests -> auto
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    "functionalTestImplementation"("org.junit.jupiter:junit-jupiter-api:5.9.3")
    "functionalTestRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    "functionalTestImplementation"(gradleTestKit())
}

val functionalTestTask = tasks.register<Test>("functionalTest") {
    dependsOn(tasks.getByName("e2immu-analyzer"))

    description = "Runs the functional tests."
    group = "verification"
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    mustRunAfter(tasks.test)
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}

tasks.check {
    dependsOn(functionalTestTask)
}

project.group = "io.codelaser.build"

e2immu {
    jmods = "java.base.jmod,java.xml.jmod"
    debugTargets = "classpath"
    sourcePackages = "org.e2immu.something.,org.e2immu.test.main"
    testSourcePackages = "org.e2immu.test.test"
    analyzedAnnotatedApiDirs = "../analyzer-shallow/e2immu-shallow-aapi/src/main/resources/json"
    excludeFromClasspath = "gradle-api-8.9.jar" // because of javax.xml.parsers.*
}

// no logic for publishing!
