plugins {
    alias(libs.plugins.fpgradle)
    alias(libs.plugins.kotlin)
}

group = "com.falsepattern"

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

    shadow {
        relocate = true
        minimize = true
    }

    tokens {
        tokenClass = "Tags"
    }

    publish {
        maven {
            repoName = "mavenpattern"
            repoUrl = "https://mvn.falsepattern.com/releases"
        }
    }
}

repositories {
    exclusive(mavenpattern(), "com.falsepattern")
}

val depLoader = configurations.register("deploader")

tasks.processResources {
    from(depLoader) {
        rename { "fplib_deploader.jar" }
    }
}

println()
tasks.shadowJar {
    val root = minecraft_fp.mod.rootPkg.map { it.replace('.', '/') }
    exclude("it/unimi/dsi/fastutil/**/package-info.class")
}

dependencies {
    compileOnlyApi(libs.kotlin.stdlib)
    compileOnlyApi(libs.kotlin.stdlibJdk8)
    compileOnlyApi(libs.kotlin.reflect)
    compileOnlyApi(libs.jetbrains.annotations)
    compileOnlyApi(libs.kotlinx.coroutinesCore)
    compileOnlyApi(libs.kotlinx.coroutinesJdk8)
    compileOnlyApi(libs.kotlinx.serializationCore)
    shadowImplementation(libs.fastutil)

    //Deploader
    depLoader(variantOf(libs.fplib) { classifier("deploader") })
    shadowImplementation(variantOf(libs.fplib) { classifier("deploader_stub") })
}

tasks.processResources {
    val versions = mapOf(
        "versionKotlin" to libs.versions.kotlin,
        "versionAnnotations" to libs.versions.annotations,
        "versionCoroutines" to libs.versions.coroutines,
        "versionSerialization" to libs.versions.serialization,
    )
    inputs.property("forgelinVersions", versions)
    filesMatching("META-INF/kotlindeps.json") {
        expand(versions.map { (name, ver) -> name to ver.get() }.toMap())
    }
}