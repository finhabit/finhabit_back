package com.ll.finhabit.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Hello API", description = "기본 서버 동작 및 상태 확인 API")
public class HelloController {

    @Operation(summary = "서버 상태 확인", description = "CI/CD, 배포, 서버 상태 점검 시 호출하는 단순 테스트 API입니다.")
    @GetMapping("/api/hello")
    public String hello() {
        return "🚀 PR CICD Test -- Hello! Finhabit Back-end is running!";
    }
}
