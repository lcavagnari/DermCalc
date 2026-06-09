plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dependency.check)

    id("org.jetbrains.dokka") version "2.2.0"
    alias(libs.plugins.kotlin.android)
}

fun stringProperty(name: String): String? = (findProperty(name) as String?) ?: System.getenv(name)

val releaseKeystorePath = stringProperty("ANDROID_SIGNING_KEYSTORE_PATH")
val releaseKeystorePassword = stringProperty("ANDROID_SIGNING_KEYSTORE_PASSWORD")
val releaseKeyAlias = stringProperty("ANDROID_SIGNING_KEY_ALIAS")
val releaseKeyPassword = stringProperty("ANDROID_SIGNING_KEY_PASSWORD")
val hasReleaseSigningConfig = listOf(
    releaseKeystorePath,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

val appVersionName: String = run {
    val raw = rootProject.file("VERSION").readText().trim()
    require(Regex("""^\d+\.\d+\.\d+$""").matches(raw)) {
        "VERSION file must contain MAJOR.MINOR.PATCH (e.g. 1.0.0), got: '$raw'"
    }
    raw
}

gradle.taskGraph.whenReady {
    if (hasTask(":app:assembleRelease") && !hasReleaseSigningConfig) {
        throw GradleException(
            "Cannot assemble release: signing config is missing. " +
            "Set ANDROID_SIGNING_KEYSTORE_PATH, ANDROID_SIGNING_KEYSTORE_PASSWORD, " +
            "ANDROID_SIGNING_KEY_ALIAS, and ANDROID_SIGNING_KEY_PASSWORD."
        )
    }
}

dependencyCheck {
    // Fail the build if any dependency has a CVSS score >= 7.0 (HIGH or CRITICAL).
    failBuildOnCVSS = 7.0f
    // Use NVD API key from environment variable to avoid rate-limiting on the NVD data feed.
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""
    // Suppress known false positives or accepted risks.
    suppressionFile = "dependency-check-suppressions.xml"
    // Output the HTML report to the standard reports directory.
    outputDirectory = layout.buildDirectory.dir("reports").get().asFile.absolutePath
    formats = listOf("HTML")
}

android {
    namespace = "it.lcavagnari.pdm.dermcalc"
    compileSdk = 36

    defaultConfig {
        applicationId = "it.lcavagnari.pdm.dermcalc"
        minSdk = 24
        targetSdk = 36
        versionCode = (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()) ?: 1
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigningConfig) {
            create("release") {
                storeFile = file(releaseKeystorePath!!)
                storePassword = releaseKeystorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    lint {
        // Reference the lint configuration XML for per-rule severity overrides.
        lintConfig = file("lint.xml")
        // Abort the build if any of the rules in lint.xml produce an error.
        abortOnError = true
        // Emit a baseline so that pre-existing findings are not counted as new failures.
        baseline = file("lint-baseline.xml")
        // Always generate the HTML report (useful for artifact upload in CI).
        htmlReport = true
        htmlOutput = layout.buildDirectory.file("reports/lint-results-debug.html").get().asFile
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
    implementation(libs.androidx.appcompat)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // room

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // new

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.kotlinx.datetime)
}
