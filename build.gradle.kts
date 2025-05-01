
tasks.register("test") {
    dependsOn(gradle.includedBuilds.map { it.task(":test") })
}
tasks.register("clean") {
    dependsOn(gradle.includedBuilds.map { it.task(":clean") })
}
tasks.register("publish") {
     dependsOn(gradle.includedBuild("e2immu-run-config").task(":publish"))
     dependsOn(gradle.includedBuild("e2immu-run-main").task(":publish"))
}
