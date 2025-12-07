package com.metrifuge.LogSimulator.config;

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
    public OpenAPI todoApiOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setEmail("metrifuge@example.com");
        contact.setName("Metrifuge Team");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Todo Application API")
                .version("1.0.0")
                .contact(contact)
                .description("A comprehensive RESTful API for managing todos with extensive logging capabilities. " +
                        "This API provides full CRUD operations, filtering, searching, and statistics for todo items. " +
                        "Built with Spring Boot and designed for log aggregation and monitoring in Kubernetes environments.")
                .termsOfService("https://example.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
