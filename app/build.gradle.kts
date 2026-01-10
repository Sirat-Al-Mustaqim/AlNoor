plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.siratalmustaqim.alnoor"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.siratalmustaqim.alnoor"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // For release, testOnly should be false (production app)
            manifestPlaceholders["testOnly"] = "false"
        }
        debug {
            applicationIdSuffix = ".dev"
            versionNameSuffix = ".dev"
            isDebuggable = true
            // For debug, testOnly=true to allows disable device owner after enabling it
            manifestPlaceholders["testOnly"] = "true"
        }
    }

    val javaVersion = libs.versions.java.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }
    kotlin {
        jvmToolchain(javaVersion)
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Timber
    implementation(libs.timber)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// Device Owner management tasks for debug builds
val packageName = "com.siratalmustaqim.alnoor"
val debugPackageName = "$packageName.dev"
val adminReceiver = "$packageName.admin.AlNoorDeviceAdminReceiver"

tasks.register("removeDeviceOwner") {
    group = "device admin"
    description = "Remove device owner before installing (pre-install)"
    doLast {
        val process = ProcessBuilder(
            "adb", "shell", "dpm", "remove-active-admin", "$debugPackageName/$adminReceiver"
        ).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        if (exitCode == 0 || output.contains("Unknown admin")) {
            println("Device owner removed (or was not set)")
        } else {
            println("Note: Could not remove device owner - $output")
        }
    }
}

tasks.register("setDeviceOwner") {
    group = "device admin"
    description = "Set device owner after installing (post-install)"
    doLast {
        val process = ProcessBuilder(
            "adb", "shell", "dpm", "set-device-owner", "$debugPackageName/$adminReceiver"
        ).redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        if (exitCode == 0) {
            println("Device owner set successfully")
        } else {
            println("Failed to set device owner: $output")
            println("Make sure there are no accounts on the device")
        }
    }
}

// Hook into debug install tasks
afterEvaluate {
    // We use named() for lazy configuration
    tasks.named("installDebug") {
        dependsOn("removeDeviceOwner")
        finalizedBy("setDeviceOwner")
    }
}