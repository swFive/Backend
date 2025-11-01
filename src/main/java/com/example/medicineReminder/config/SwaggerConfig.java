package com.example.medicineReminder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig
{
    @Bean
    public OpenAPI openAPI()
    {
        return new OpenAPI().info(new Info()
                .title("Medicine Reminder API")
                .description("스프링부트 3.x · JPA · FCM")
                .version("v1"));
    }
}
