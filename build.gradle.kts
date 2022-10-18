plugins {
    java
    application
}

group = "be.rm.secu.tp1"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("be.rm.secu.tp1.Program")
}

dependencies {
    implementation("com.google.inject:guice:5.1.0")

    implementation("info.picocli:picocli:4.6.3")
    annotationProcessor("info.picocli:picocli-codegen:4.6.3")

    implementation("io.reactivex.rxjava3:rxjava:3.1.5")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
