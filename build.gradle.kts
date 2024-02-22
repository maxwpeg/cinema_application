plugins {
    application
    kotlin("jvm") version "1.9.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
}
