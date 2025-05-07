
tasks.register("test") {
    dependsOn(gradle.includedBuilds.map { it.task(":test") })
}
tasks.register("clean") {
    dependsOn(gradle.includedBuilds.map { it.task(":clean") })
}
tasks.register("publish") {
     dependsOn(gradle.includedBuild("e2immu-run-config").task(":publish"))
     dependsOn(gradle.includedBuild("e2immu-run-main").task(":publish"))
     dependsOn(gradle.includedBuild("e2immu-run-gradleplugin").task(":publish"))
}
tasks.register("publishToMavenLocal") {
     dependsOn(gradle.includedBuild("e2immu-run-config").task(":publishToMavenLocal"))
     dependsOn(gradle.includedBuild("e2immu-run-main").task(":publishToMavenLocal"))
     dependsOn(gradle.includedBuild("e2immu-run-gradleplugin").task(":publishToMavenLocal"))
}
