package com.ll.finhabit.domain.auth.controller;

import com.ll.finhabit.domain.auth.dto.LoginRequest;
import com.ll.finhabit.domain.auth.dto.LoginResponse;
import com.ll.finhabit.domain.auth.dto.SignupRequest;
import com.ll.finhabit.domain.auth.dto.SignupResponse;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LevelTestRepository levelTestRepository;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest req) {
        return ResponseEntity.ok(authService.signup(req));
    }

    @GetMapping("/leveltest")
    public List<LevelTest> getLevelTests() {
        return levelTestRepository.findAll();
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        LoginResponse res = authService.login(request);

        HttpSession session = httpRequest.getSession();
        session.setAttribute("LOGIN_USER_ID", res.getId());

        return res;
    }
}
