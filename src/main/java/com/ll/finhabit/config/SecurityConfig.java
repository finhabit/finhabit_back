package com.ll.finhabit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("https://www.finhabit.shop") // 프론트/Swagger 접근 URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs",
                                "/api/auth/login",        // 만약 REST 로그인 API
                                "/api/auth/signup"      // 만약 REST 회원가입 API
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin()    // 폼 로그인 활성화 (페이지 직접 노출)
        // .csrf().disable()  // REST API만 사용하면 개발 중에만 잠깐 끄기, 일반적으로는 켬
        ;
        return http.build();
    }
}
