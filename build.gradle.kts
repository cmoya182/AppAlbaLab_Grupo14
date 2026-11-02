// Archivo de construcción principal del proyecto
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Plugin KSP necesario para procesar las anotaciones de Room
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}