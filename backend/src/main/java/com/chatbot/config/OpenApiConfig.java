package com.chatbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Status Chatbot API")
                        .description("AI-Powered Order Status Chatbot Backend API for retrieving order information and managing customer interactions.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Chatbot Development Team")
                                .email("support@chatbot.com")
                                .url("https://github.com/chatbot-challenge"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.chatbot.com")
                                .description("Production Server")
                ));
    }
} 