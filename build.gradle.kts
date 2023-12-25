plugins {
    kotlin("jvm") version "1.9.21"
    application
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

application {
    //mainClass.set("Day01Kt")
    //mainClass.set("Day02Kt")
    //mainClass.set("Day03Kt")
    //mainClass.set("Day04Kt")
    //mainClass.set("Day05Kt")
    //mainClass.set("Day06Kt")
    //mainClass.set("Day07Kt")
    //mainClass.set("Day08Kt")
    //mainClass.set("Day09Kt")
    //mainClass.set("Day10Kt")
    //mainClass.set("Day11Kt")
    //mainClass.set("Day12Kt")
    //mainClass.set("Day13Kt")
    mainClass.set("Day14Kt")
}
