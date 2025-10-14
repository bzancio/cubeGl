plugins {
    id("java")
    application
}

group = "org.cubeGl"
version = "0.1b"

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.3"
val lwjglNatives = when {
    System.getProperty("os.name").lowercase().contains("linux") -> "natives-linux"
    System.getProperty("os.name").lowercase().contains("mac") -> "natives-macos"
    System.getProperty("os.name").lowercase().contains("windows") -> "natives-windows"
    else -> throw Error("Sistema operativo no soportado: ${System.getProperty("os.name")}")
}

dependencies {
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    implementation("org.joml:joml:1.10.5")


    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
}

application {
    mainClass.set("com.cubeGl.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}
