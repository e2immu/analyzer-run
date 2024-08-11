
tasks.register("test") {
    dependsOn(gradle.includedBuilds.map { it.task(":test") })
}
tasks.register("clean") {
    dependsOn(gradle.includedBuilds.map { it.task(":clean") })
}