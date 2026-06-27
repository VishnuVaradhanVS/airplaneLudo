plugins {
    kotlin("jvm")
    id("application")
}

application {
    mainClass.set("com.example.backend.ServerKt")
}

dependencies {
    implementation(project(":shared"))

    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("io.ktor:ktor-server-websockets-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.7")

    implementation("ch.qos.logback:logback-classic:1.4.14")
}