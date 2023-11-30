plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("jacoco")
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
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring boot JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    //MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache:2.4.0")

    // Validacion
    implementation("org.springframework.boot:spring-boot-starter-validation")

    //Websockets
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    //Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    //Spring security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // H2
    implementation("com.h2database:h2")

    // PostgreSQL
    implementation("org.postgresql:postgresql")

    //Nogociacion de Contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    // Para manejar los JWT tokens
    // JWT (Json Web Token)
    implementation("com.auth0:java-jwt:4.4.0")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Test seguridad
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    // Ponemos el perfil de test para que cargue el application-test.properties
    // para ahorranos hacer esto
    //./gradlew test -Pspring.profiles.active=dev
    systemProperty("spring.profiles.active", project.findProperty("spring.profiles.active") ?: "dev")
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.required = false
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}