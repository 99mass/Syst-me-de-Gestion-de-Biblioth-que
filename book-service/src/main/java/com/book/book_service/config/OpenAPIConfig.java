package com.book.book_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API de Gestion des Livres")
                                                .version("1.0")
                                                .description("API de gestion des livres dans une bibliothèque")
                                                .contact(new Contact()
                                                                .name("Équipe de développement")
                                                                .email("sambadiop161@gmail.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                                .servers(List.of(
                                                new Server().url("http://localhost:8080").description(
                                                                "Serveur de développement via API Gateway")));
        }
}