package com.codavert.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Codavert API")
                        .version("1.0.0")
                        .description("""
                                # Codavert Freelancing Platform API
                                
                                A comprehensive REST API for managing freelancing projects, clients, and business operations.
                                
                                ## Features
                                - **Authentication & Authorization**: JWT-based secure authentication
                                - **Client Management**: Complete CRUD operations for client data
                                - **Project Management**: Track projects, tasks, and milestones
                                - **Document Generation**: Generate MOU, SRS, and other project documents
                                - **Time Tracking**: Log and manage time entries
                                - **Financial Management**: Handle invoices and payments
                                - **Communication Hub**: Email notifications and messaging
                                
                                ## Authentication
                                Most endpoints require JWT authentication. Include the JWT token in the Authorization header:
                                ```
                                Authorization: Bearer <your-jwt-token>
                                ```
                                
                                ## Base URL
                                All API endpoints are prefixed with `/api`
                                
                                ## Response Format
                                All responses follow a consistent JSON format with appropriate HTTP status codes.
                                """)
                        .contact(new Contact()
                                .name("Codavert Team")
                                .email("support@codavert.com")
                                .url("https://codavert.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("https://codavert.onrender.com/api")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.codavert.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

