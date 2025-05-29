
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.iskportal"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    maven { url = uri("https://www.jitpack.io") }
}


dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    // Using integrations with Jupyter to interact with a session of SageMath.
    implementation("com.github.SpencerPark:jupyter-jvm-basekernel:v2.3.0")
}

tasks.withType<JavaExec> {
    // Let JVM find native libs (JNI and JEP)
    systemProperty("java.library.path", "${projectDir}/native")
}