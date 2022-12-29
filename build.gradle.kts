plugins {
    kotlin("multiplatform") version "1.8.0"
}

group = "com.patricktcoakley"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val hostArch = System.getProperty("os.arch")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> if (hostArch == "aarch64") macosArm64("native") else macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val sdl by creating
            }
        }
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.useK2 = true
    }
}
