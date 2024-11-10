plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "dev.cryptic.obscura"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.3"
val jomlVersion = "1.10.5"
val lwjglNatives = "natives-windows"

tasks.register<JavaExec>("runGame") {
    mainClass.set("dev.cryptic.obscura.Obscura")
    classpath = sourceSets["main"].runtimeClasspath
    systemProperty("game.package", "com.example.mygame") // Replace with the actual package to scan
}
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-bgfx")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-nanovg")
    implementation("org.lwjgl", "lwjgl-nuklear")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opencl")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-par")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-vulkan")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")

    implementation("org.reflections:reflections:0.10.2")

    implementation("io.github.spair:imgui-java-app:1.81.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = "dev.cryptics.obscura"
//            artifactId = "library"
//            version = "1.1"
//
//            from(components["java"])
//        }
//    }
//}

tasks.test {
    useJUnitPlatform()
}