plugins {
    alias(libs.plugins.android.application) apply false
    // Updated Kotlin to 2.0.21
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    // Aligned Compose Compiler plugin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
    // Aligned KSP version
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
}
