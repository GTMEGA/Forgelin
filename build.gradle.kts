plugins {
    alias(libs.plugins.fpgradle)
    alias(libs.plugins.kotlin)
}

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

    kotlin {
        hasKotlinDeps = true
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
    compileOnlyApi(variantOf(libs.fplib) { classifier("api") })
    runtimeOnly(variantOf(libs.fplib) { classifier("dev") })
    compileOnlyApi(libs.kotlin.stdlib)
    compileOnlyApi(libs.kotlin.stdlibJdk8)
    compileOnlyApi(libs.kotlin.reflect)
    compileOnlyApi(libs.jetbrains.annotations)
    compileOnlyApi(libs.kotlinx.coroutinesCore)
    compileOnlyApi(libs.kotlinx.coroutinesJdk8)
    compileOnlyApi(libs.kotlinx.serializationCore)
}

tasks.processResources {
    val versions = mapOf(
        "versionKotlin" to libs.versions.kotlin,
        "versionAnnotations" to libs.versions.annotations,
        "versionCoroutines" to libs.versions.coroutines,
        "versionSerialization" to libs.versions.serialization
    )
    inputs.property("forgelinVersions", versions)
    filesMatching("META-INF/kotlindeps.json") {
        expand(versions.map { (name, ver) -> name to ver.get() }.toMap())
    }
}