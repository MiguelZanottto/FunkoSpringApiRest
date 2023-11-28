package org.develop.config.swagger;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    @Value("${api.version}")
    private String apiVersion;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST Tienda Funkos 2023/2024")
                                .version("1.0.0")
                                .description("API desarrollada en clases de Desarrollo Entorno Sergvidor")
                                .termsOfService("https://github.com/MiguelZanottto")
                                .license(
                                        new License()
                                                .name("CC BY-NC-SA 4.0")
                                                .url("https://github.com/MiguelZanottto")
                                )
                                .contact(
                                        new Contact()
                                                .name("Miguel Angel Zanotto Rojas")
                                                .email("miguelangel.zanotto@alumno.iesluisvives.org")
                                                .url("https://github.com/MiguelZanottto")
                                )

                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Documentación del Proyecto")
                                .url("https://github.com/MiguelZanottto/FunkoSpringApiRest")
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub del Proyecto")
                                .url("https://github.com/MiguelZanottto/FunkoSpringApiRest")
                )
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()));
    }


    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("https")
                .pathsToMatch("/" + apiVersion + "/funkos/**")
                .displayName("API Tienda Spring Boot Funkos 2023/2024")
                .build();
    }
}