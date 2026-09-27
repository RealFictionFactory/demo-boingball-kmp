plugins {
    `kotlin-dsl`
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("appVersion") {
            id = "boingball.app-version"
            implementationClass = "boingball.version.AppVersionPlugin"
        }
    }
}
