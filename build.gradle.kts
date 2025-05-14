plugins {
    id("com.falsepattern.fpgradle-mc") version "0.15.1"
    kotlin("jvm") version "2.1.21"
}

val kotlinVersion = "2.1.21"
val coroutinesVersion = "1.10.2"
val serializationVersion = "1.8.1"

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
    apiSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.5.10")
    compileOnlyApi("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    compileOnlyApi("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnlyApi("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    compileOnlyApi("org.jetbrains:annotations:26.0.2")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${coroutinesVersion}")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-serialization-core:${serializationVersion}")
}
