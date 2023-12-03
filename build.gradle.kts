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
    mainClass.set("Day02Kt")
}
