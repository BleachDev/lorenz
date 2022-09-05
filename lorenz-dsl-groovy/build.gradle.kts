plugins {
    `java-library`
}

val groovyVersion: String by rootProject

dependencies {
    implementation("org.apache.groovy:groovy:$groovyVersion")
    api(project(":lorenz"))
}

tasks.jar.configure {
    manifest.attributes(mapOf("Automatic-Module-Name" to "${project.group}.lorenz.dsl.groovy"))
}
