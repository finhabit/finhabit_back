package com.ll.finhabit.domain.auth.controller;

import com.ll.finhabit.domain.auth.dto.SignupRequest;
import com.ll.finhabit.domain.auth.dto.SignupResponse;
import com.ll.finhabit.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }
}
