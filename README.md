# Forgelin 2 <img src=".idea/icon.png" align="right" width=150>

<sup>**[CurseForge](https://www.curseforge.com/minecraft/mc-mods/forgelin-legacy) | [Modrinth](https://modrinth.com/mod/forgelin-legacy)**</sup>

Fork of [shadowfacts Forgelin](https://github.com/shadowfacts/Forgelin).

## Additions
- Backport to 1.7.10
- Updated to Kotlin 2.x
- Dynamic kotlin downloading via FalsePatternLib (much smaller mod jar size!)
- Provides a Forge `ILanguageAdapter` for using Kotlin `object` classes as your main mod class.
- Provides Dispatchers.MinecraftServer and Dispatchers.MinecraftClient for dispatching coroutines during server tick and client tick, respectively

## Usage with [FPGradle](https://github.com/Falsepattern/Examplemod)

build.gradle.kts:
```kotlin
plugins {
    //...
    kotlin("jvm") version "2.2.21"
}
minecraft_fp {
    //...
    kotlin {
        forgelinVersion = "2.4.0-2.2.21"
    }
}
```
gradle.properties:
```properties
kotlin.stdlib.default.dependency=false
```

<!--TODO: guide without fpgradle-->

The kotlin version that forgelin downloads is always in the suffix after the `-` in the version number
