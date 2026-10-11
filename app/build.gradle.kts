plugins { id("com.android.application") }
android {
    namespace = "com.hernandovela.reto7"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.hernandovela.reto7"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.hernandovela.reto7.OnlineSmokeInstrumentation"
    }
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

