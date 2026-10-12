plugins { id("com.android.application") }
android {
    namespace = "com.hernandovela.reto7"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.hernandovela.reto7"
        minSdk = 24
        targetSdk = 36
        versionCode = 5
        versionName = "1.3.0"
        testInstrumentationRunner = "com.hernandovela.reto7.OnlineSmokeInstrumentation"
    }
    signingConfigs.getByName("debug") { enableV1Signing = true; enableV2Signing = true }
    buildTypes { create("distribution") { initWith(getByName("release")); signingConfig = signingConfigs.getByName("debug"); isDebuggable = false; isMinifyEnabled = false } }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
dependencies { testImplementation("junit:junit:4.13.2"); implementation(platform("com.google.firebase:firebase-bom:34.4.0")); implementation("com.google.firebase:firebase-auth"); implementation("com.google.firebase:firebase-firestore") }

// Keep the downloaded configuration local; package it as an asset for explicit initialization.
val firebaseAssets = layout.buildDirectory.dir("generated/firebaseAssets")
val copyFirebaseConfig by tasks.registering(Copy::class) {
    from(layout.projectDirectory.file("google-services.json"))
    into(firebaseAssets)
}
android.sourceSets.getByName("main").assets.srcDir(firebaseAssets.get().asFile)
tasks.named("preBuild").configure { dependsOn(copyFirebaseConfig) }

