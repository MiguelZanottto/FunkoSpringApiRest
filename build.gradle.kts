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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache:2.4.0")
    //Nogociacion de Contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    //Websockets
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    //MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    //Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    //Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Dependencia para test
    testImplementation("org.springframework.security:spring-security-test")
    // Para manejar los JWT tokens
    // JWT (Json Web Token)
    implementation("com.auth0:java-jwt:4.4.0")
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
