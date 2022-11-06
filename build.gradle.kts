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

tasks.withType<Jar>() {
    manifest {
        attributes("Main-Class" to "be.rm.secu.tp1.Program")
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    manifest {
        attributes("Main-Class" to "be.rm.secu.tp1.Program")
    }

    archiveBaseName.set("${project.name}-all")

    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE;

    with(tasks.jar.get() as CopySpec)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
