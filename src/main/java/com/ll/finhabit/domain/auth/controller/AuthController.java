package com.ll.finhabit.domain.auth.controller;

import com.ll.finhabit.domain.auth.dto.LoginRequest;
import com.ll.finhabit.domain.auth.dto.LoginResponse;
import com.ll.finhabit.domain.auth.dto.SignupRequest;
import com.ll.finhabit.domain.auth.dto.SignupResponse;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LevelTestRepository levelTestRepository;

    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @GetMapping("/leveltest")
    public List<LevelTest> getLevelTests() {
        return levelTestRepository.findAll();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
