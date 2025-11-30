package com.ll.finhabit.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Swagger UI 기본 그룹 설정
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder().group("finhabit").pathsToMatch("/api/**").build();
    }

    // Swagger UI 프로퍼티 주입
    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(
            SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return new SwaggerUiConfigParameters(swaggerUiConfigProperties);
    }
}
