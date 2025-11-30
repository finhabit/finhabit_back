package com.ll.finhabit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI openAPI() {
        Info info =
                new Info().title("Finhabit API").version("v1.0.0").description("Finhabit API Docs");

        // 환경별 서버 URL 설정
        Server server;
        if ("prod".equals(activeProfile)) {
            server = new Server();
            server.setUrl("https://www.finhabit.shop"); // 운영 도메인
            server.setDescription("Production Server");
        } else {
            server = new Server();
            server.setUrl("/"); // 로컬 개발
            server.setDescription("Development Server");
        }

        return new OpenAPI().info(info).servers(List.of(server));
    }
}
