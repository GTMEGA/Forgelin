plugins {
    id("fpgradle-minecraft") version "0.10.4-dev2"
    kotlin("jvm") version "2.1.10"
}

val kotlinVersion = "2.1.10"
val coroutinesVersion = "1.10.1"

group = "mega"

minecraft_fp {
    mod {
        modid = "forgelin"
        name = "Forgelin 2"
        rootPkg = "net.shadowfacts.forgelin"
    }
    core {
        coreModClass = "preloader.ForgelinPlugin"
    }

    tokens {
        tokenClass = "Tags"
    }

    publish {
        maven {
            repoName = "mega"
            repoUrl = "https://mvn.falsepattern.com/gtmega_releases"
        }
    }
}

repositories {
    exclusive(mavenpattern(), "com.falsepattern")
}

dependencies {
    implementationSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.5.9")
    api("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    api("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    api("org.jetbrains:annotations:26.0.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${coroutinesVersion}")
}
