package com.foodsave.platform.shared.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI foodSaveOpenApi() {
        return new OpenAPI().info(new Info()
                .title("FoodSave API")
                .version("0.1.0")
                .description("REST API for the FoodSave web application."))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }

    @Bean
    OpenApiCustomizer versionedApiPathsOnly() {
        return openApi -> openApi.getPaths().entrySet().removeIf(entry ->
                !entry.getKey().startsWith("/api/v1/"));
    }
}
