# Forgelin 2
Fork of [shadowfacts Forgelin](https://github.com/shadowfacts/Forgelin).

## Additions
- Backport to 1.7.10
- Updated to Kotlin 2.x
- Dynamic kotlin downloading via FalsePatternLib (much smaller mod jar size!)
- Provides a Forge `ILanguageAdapter` for using Kotlin `object` classes as your main mod class.

## Usage with [FPGradle](https://github.com/Falsepattern/Examplemod)
```kotlin
plugins {
    //...
    kotlin("jvm") version "LATEST_KOTLIN_VERSION"
}
minecraft_fp {
    //...
    kotlin {
        version = "LATEST_VERSION"
    }
}
```

The kotlin version that forgelin downloads is always in the suffix after the `-` in the version number