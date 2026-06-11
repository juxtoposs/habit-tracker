package com.example.habittracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for REST API documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates an OpenAPI bean with application metadata.
     *
     * @return configured OpenAPI object
     */
    @Bean
    public OpenAPI habitTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Habit Tracker API")
                        .description("RESTful Web Service for tracking habits (Richardson Level 4)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Student Developer")
                                .email("student@example.com")));
    }
}
