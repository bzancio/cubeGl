plugins {
    id("java")
    application
}

group = "org.cubeGl"
version = "0.1b"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.lwjgl:lwjgl:3.3.1")
    implementation("org.lwjgl:lwjgl-glfw:3.3.1")
    implementation("org.lwjgl:lwjgl-opengl:3.3.1")
    implementation("org.lwjgl:lwjgl-stb:3.3.1")
}

application {
    mainClass.set("com.cubeGl.Main")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
