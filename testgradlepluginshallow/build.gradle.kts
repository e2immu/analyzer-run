plugins {
    java
    id("org.e2immu.analyzer-plugin")
}

group = "io.codelaser.build"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    maven {
        url = uri(project.findProperty("codeartifactPublicUri") as String)
        credentials {
            username = "aws"
            password = project.findProperty("codeartifactToken") as String
        }
    }
    mavenCentral()
}


// see https://docs.gradle.org/current/userguide/testing_gradle_plugins.html
val functionalTest: SourceSet by sourceSets.creating

val jupiterApiVersion = project.findProperty("jupiterApiVersion") as String
val jupiterEngineVersion = project.findProperty("jupiterEngineVersion") as String

dependencies {
    implementation(gradleApi())

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterApiVersion") // used in tests -> auto
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterEngineVersion")

    "functionalTestImplementation"("org.junit.jupiter:junit-jupiter-api:$jupiterApiVersion")
    "functionalTestRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:$jupiterEngineVersion")
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

// we're not executing the tests in "test", they're there to provide .class files to analyze!!

tasks.check {
    dependsOn(functionalTestTask)
}

project.group = "io.codelaser.build"

e2immu {
    jmods = "java.base.jmod"
    debugTargets = "classpath"
    // the following directory does not exist, so we're working with the 2 test sources only
    sourcePackages = "org.e2immu.testpackages"
    testSourcePackages = "org.e2immu.testwrite"
    analyzedAnnotatedApiTargetDir = "/tmp/testWriteAnalyzedAnnotatedAPIDir"
    analyzedAnnotatedApiDirs = "testgradlepluginshallow/src/main/resources/json"
}

// no logic for publishing!
