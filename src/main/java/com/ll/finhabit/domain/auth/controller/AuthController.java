package com.ll.finhabit.domain.auth.controller;

import com.ll.finhabit.domain.auth.dto.LoginRequest;
import com.ll.finhabit.domain.auth.dto.LoginResponse;
import com.ll.finhabit.domain.auth.dto.SignupRequest;
import com.ll.finhabit.domain.auth.dto.SignupResponse;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "회원 가입 / 로그인 / 레벨 테스트 API")
public class AuthController {

    private final AuthService authService;
    private final LevelTestRepository levelTestRepository;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "닉네임, 이메일, 비밀번호와 5문항 레벨 테스트 답안을 받아 회원을 생성하고, 정답 개수에 따라 레벨을 부여한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "이메일 형식 오류 또는 비밀번호 불일치, 존재하지 않는 문제 ID"),
        @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest req) {
        return ResponseEntity.ok(authService.signup(req));
    }

    @GetMapping("/leveltest")
    @Operation(summary = "레벨 테스트 문제 조회", description = "회원가입 시 사용하는 레벨 테스트 문제 전체를 조회한다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "레벨 테스트 문제 조회 성공")})
    public List<LevelTest> getLevelTests() {
        return levelTestRepository.findAll();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고, 세션에 LOGIN_USER_ID를 저장한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
        @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않음")
    })
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        LoginResponse res = authService.login(request);

        HttpSession session = httpRequest.getSession();
        session.setAttribute("LOGIN_USER_ID", res.getId());

        return res;
    }
}
