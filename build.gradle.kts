@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4-M1"
  antlr
  application
}

group = "org.cottand"
version = "1.0-SNAPSHOT"

repositories {
  maven("https://dl.bintray.com/kotlin/kotlin-eap")
  mavenCentral()
}

dependencies {
  val antlrVer = "4.7"
  val junitVer = "5.6.0"
  implementation(kotlin("stdlib-jdk8"))
  antlr("org.antlr:antlr4:$antlrVer")
  // JUnit5
  testImplementation("org.junit.jupiter:junit-jupiter:$junitVer")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVer")

}
//// Antlr compile grammar task output directory, and add it to the java sourceSets
//val antlrOut = "build/generated/source/antlr/".also {
//  sourceSets["main"].java.srcDir(it)
//}
//
//
//
//tasks {
//  generateGrammarSource {
//    val generatedPackage = "antlr"
//    arguments =
//      arguments + listOf("-package", generatedPackage, "-visitor", "-no-listener", "-Werror")
//    outputDirectory = file(antlrOut + generatedPackage)
//  }
tasks {
  generateGrammarSource {
    arguments = arguments + listOf("-no-listener", "-visitor", "-Werror")
  }
  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
    }
    dependsOn(generateGrammarSource)
  }
}