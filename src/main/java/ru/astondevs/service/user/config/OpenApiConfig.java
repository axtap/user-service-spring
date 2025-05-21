package ru.astondevs.service.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "User-service api",
                description = "API системы для управления пользователями",
                version = "1.0.0",
                contact = @Contact(
                        name = "Akhtariev Rinat",
                        email = "axtap@mail.ru"
                )
        )
)
public class OpenApiConfig {
}
