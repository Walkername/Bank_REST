package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Cards Management API")
                        .version("1.0")
                        .description("API for managing bank cards with admin functionality")
                        .contact(new Contact()
                                .name("API Support")
                                .email("api@example.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

}
