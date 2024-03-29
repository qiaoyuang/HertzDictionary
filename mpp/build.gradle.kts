plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("com.android.library")
    id("kotlin-parcelize")
}

group = "com.w10group.hertzdictionary"
version = "1.0.0"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

val kotlinVersion = "1.6.20"
val coroutinesVersion = "1.6.1"
val serializationVersion = "1.3.2"
val ktorVersion = "2.0.0"
val roomVersion = "2.4.2"

kotlin {
    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
    android("android") {
        // publishLibraryVariants("release", "debug")
    }
    iosX64("ios") {
        binaries {
            framework {
                baseName = "library"
                isStatic = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin 扩展类库
                api(kotlin("stdlib"))
                // api("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

                // Ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(fileTree("include" to listOf("*.jar"), "dir" to "libs"))
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

                // Ktor
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                // Room
                implementation("androidx.room:room-runtime:$roomVersion")
                implementation("androidx.room:room-ktx:$roomVersion")
                compileOnly("javax.annotation:javax.annotation-api:1.3.2")

                // Square
                implementation("com.squareup.okio:okio:3.0.0")
                implementation("com.squareup.okhttp3:okhttp:4.9.3")
            }
        }
        val iosMain by getting {
            dependencies {
                // Ktor
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidTest by getting
        val iosTest by getting
    }
}

android {
    compileSdk = 31
    buildToolsVersion = "32.1.0-rc1"
    defaultConfig {
        minSdk = 23
        targetSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mutableMapOf("room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"))
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        getByName("main") {
            manifest.srcFile(File("src/androidMain/AndroidManifest.xml"))
            java.setSrcDirs(listOf("src/androidMain/kotlin"))
            res.setSrcDirs(listOf("src/androidMain/res"))
            assets.setSrcDirs(listOf("src/androidMain/assets"))
        }
    }
}

dependencies {
    "kapt"("androidx.room:room-compiler:$roomVersion")
}