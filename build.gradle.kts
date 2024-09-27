
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.2")
        classpath  ("com.google.gms:google-services:4.4.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    }
