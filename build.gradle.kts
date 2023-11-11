plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "org.develop"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    // Websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // Para pasar a XML los responses, negocacion de contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
