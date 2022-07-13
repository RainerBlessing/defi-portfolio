import net.ltgt.gradle.errorprone.errorprone
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion = "1.6.0"
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath(kotlin("gradle-plugin", version = "1.6.21"))
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.7.4")
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

plugins {
    id("java")
    id("net.ltgt.errorprone") version "2.0.2"
    id("org.openjfx.javafxplugin") version "0.0.13"

    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose")

    id("info.solidsoft.pitest") version "1.7.4"
}

dependencies {
    implementation ("com.googlecode.json-simple:json-simple:1.1.1")
    errorprone ("com.google.errorprone:error_prone_core:${errorproneVersion}")
    implementation(files("lib/CoinGecko-Java-master.jar"))
    implementation ("com.google.inject:guice:5.1.0")
    implementation("dev.misfitlabs.kotlinguice4:kotlin-guice:1.6.0")
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

configure<info.solidsoft.gradle.pitest.PitestPluginExtension>  {
    targetClasses.set(setOf("org.defichain.portfolio.*"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}