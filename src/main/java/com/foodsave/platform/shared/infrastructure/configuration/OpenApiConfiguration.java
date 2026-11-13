package com.foodsave.platform.shared.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    OpenAPI foodSaveOpenApi() {
        return new OpenAPI().info(new Info()
                .title("FoodSave API")
                .version("0.1.0")
                .description("REST API for the FoodSave web application."));
    }
}
