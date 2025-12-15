package com.ll.finhabit.domain.auth.controller;

import com.ll.finhabit.domain.auth.dto.LoginRequest;
import com.ll.finhabit.domain.auth.dto.LoginResponse;
import com.ll.finhabit.domain.auth.dto.SignupRequest;
import com.ll.finhabit.domain.auth.dto.SignupResponse;
import com.ll.finhabit.domain.auth.dto.UserMeUpdateDto;
import com.ll.finhabit.domain.auth.dto.UserPasswordUpdateDto;
import com.ll.finhabit.domain.auth.dto.UserProfileResponseDto;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.service.AuthService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원 가입 / 로그인 / 레벨 테스트 API / 마이페이지 ")
public class AuthController {

    private final AuthService authService;
    private final LevelTestRepository levelTestRepository;
    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

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

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 세션을 무효화(invalidate)해서 로그아웃 처리한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공(세션 삭제)"),
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false); // 없으면 새로 만들지 않음
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "내 로그인 상태 확인", description = "세션에 저장된 LOGIN_USER_ID를 반환한다. (없으면 null)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    public ResponseEntity<Long> me(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return ResponseEntity.ok(null);
        }

        Object userId = session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok((Long) userId);
    }

    @Operation(summary = "GET /auth/me/profile: 내 프로필 정보 조회")
    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @Parameter(hidden = true) @CurrentUser Long userId) {

        UserProfileResponseDto dto = authService.getProfile(userId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "PATCH /auth/me/profile: 내 프로필 (닉네임, 이메일) 수정")
    @PatchMapping("/me/profile")
    public ResponseEntity<Void> updateProfile(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Valid @RequestBody UserMeUpdateDto dto) {

        authService.updateProfile(userId, dto);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "PATCH /auth/me/password: 비밀번호 변경")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Valid @RequestBody UserPasswordUpdateDto dto) {

        authService.updatePassword(userId, dto);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/me/withdraw")
    @Operation(summary = "회원탈퇴", description = "현재 로그인한 사용자의 계정을 삭제한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
        @ApiResponse(responseCode = "401", description = "로그인 상태가 아님"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteMe(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(LOGIN_USER_ID) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);

        authService.deleteUser(userId);

        session.invalidate();

        return ResponseEntity.noContent().build();
    }
}
