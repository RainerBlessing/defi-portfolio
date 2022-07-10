import net.ltgt.gradle.errorprone.errorprone

buildscript {
    val kotlinVersion = "1.6.0"
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath(kotlin("gradle-plugin", version = "1.6.21"))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

val errorproneVersion = "2.14.0"
val kotlinVersion = "1.6.10"
val composeVersion = "1.0.0-beta06"

plugins {
    id("java")
    id("net.ltgt.errorprone") version "2.0.2"
    id("org.openjfx.javafxplugin") version "0.0.13"

    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev686"
}

dependencies {
    implementation ("com.googlecode.json-simple:json-simple:1.1.1")
    errorprone ("com.google.errorprone:error_prone_core:${errorproneVersion}")
    implementation(files("lib/CoinGecko-Java-master.jar"))
    implementation ("com.google.inject:guice:5.1.0")
    implementation ("com.cathive.fx:fx-guice:8.0.0")

    implementation ("org.slf4j:slf4j-simple:2.0.0-alpha7")
    implementation ("com.opencsv:opencsv:5.6")

    testImplementation ("org.testng:testng:7.6.0")
    testImplementation ("org.mockito:mockito-all:1.10.19")
    implementation(kotlin("stdlib-jdk8"))

    implementation(compose.desktop.currentOs)
}

javafx {
    version = "12"
    modules ("javafx.controls", "javafx.fxml")
}

tasks.test {
    useTestNG()
}

tasks.withType<JavaCompile>().configureEach  {
    options.errorprone {
      option("excludedPaths",".*/controllers/.*|.*/models/.*|.*/provider/.*|.*/resourceprovider/.*|.*/services/.*|.*/views/.*|.*/AppModule.java|.*/Main.java")
    }
}

compose.desktop {
    application {
        mainClass = "org.defichain.portfolio.MainKt"
    }
}