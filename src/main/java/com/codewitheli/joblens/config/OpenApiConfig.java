package com.codewitheli.joblens.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API metadata for the Swagger UI (served at /swagger-ui.html).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI jobLensOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("JobLens API")
                .version("0.1.0")
                .description("""
                        AI job-application assistant. Analyze a job description against a
                        skills-only profile (supplied per request, never stored), import a
                        posting from text/PDF/URL, and track applications on a board.""")
                .license(new License().name("MIT").url("https://github.com/dev-codewitheli/joblens/blob/main/LICENSE")));
    }
}
