package ru.tveu.shiftcrm.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Shift CRM API", version = "v1", description = "Документация API для Shift CRM"))
public class OpenApiConfig {
}
