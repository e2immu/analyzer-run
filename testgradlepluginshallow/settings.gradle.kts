rootProject.name="testgradlepluginshallow"

pluginManagement {
    includeBuild("../e2immu-run-gradleplugin")

    val codeartifactUri: String by settings
    val codeartifactToken: String by settings

    repositories {
        maven {
            url = uri(codeartifactUri)
            credentials {
                username = "aws"
                password = codeartifactToken
            }
        }
        mavenCentral()
    }
}
