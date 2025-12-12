package com.ll.finhabit.global.config;

import com.ll.finhabit.global.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 배포 도메인
                .allowedOrigins("https://finhabit.shop", "https://www.finhabit.shop")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**") // 기본적으로 모든 요청에 인터셉터 적용
                .excludePathPatterns(
                        // 인증 없이 허용할 것들

                        "/api/hello",

                        // auth 관련 (회원가입/로그인/레벨테스트)
                        "/api/auth/**",

                        // Swagger / OpenAPI
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api-docs/**",

                        // 에러 페이지
                        "/error",

                        // 메인 및 정적 리소스
                        "/",
                        "/favicon.ico",
                        "/css/**",
                        "/js/**",
                        "/images/**");
    }
}
