import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "test.android.u2f"
    compileSdk = 35

    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = compileSdk
        versionCode = 1
        versionName = "0.0.$versionCode"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".$name"
            versionNameSuffix = "-$name"
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    buildFeatures.buildConfig = true
}

androidComponents.onVariants { variant ->
    val output = variant.outputs.single()
    check(output is com.android.build.api.variant.impl.VariantOutputImpl)
    output.outputFileName = listOf(
        rootProject.name,
        android.defaultConfig.versionName!!,
        variant.name,
        android.defaultConfig.versionCode!!.toString(),
    ).joinToString(separator = "-", postfix = ".apk")
    afterEvaluate {
        tasks.getByName<JavaCompile>("compile${variant.name.replaceFirstChar(Character::toUpperCase)}JavaWithJavac") {
            targetCompatibility = "17"
        }
        tasks.getByName<KotlinCompile>("compile${variant.name.replaceFirstChar(Character::toUpperCase)}Kotlin") {
            compilerOptions.jvmTarget = JvmTarget.fromTarget("17")
        }
    }
}

dependencies {
    // todo
}
