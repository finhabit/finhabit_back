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
@Tag(name = "Auth", description = "회원 가입 / 로그인 / 레벨 테스트 / 마이페이지 API")
public class AuthController {

    private final AuthService authService;
    private final LevelTestRepository levelTestRepository;

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = """
                    신규 사용자를 등록합니다.
                    
                    - 닉네임, 이메일, 비밀번호를 필수로 입력해야 합니다.
                    - 비밀번호는 8~16자의 영문, 숫자, 특수문자 조합이어야 합니다.
                    - 레벨 테스트 답안(선택)을 제출하면 정답 개수에 따라 초기 레벨이 결정됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 (이메일 형식 오류, 비밀번호 정책 위반, 비밀번호 불일치, 존재하지 않는 문제 ID)"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest req
    ) {
        return ResponseEntity.ok(authService.signup(req));
    }

    @GetMapping("/leveltest")
    @Operation(
            summary = "레벨 테스트 문제 조회",
            description = """
                    회원가입 시 사용하는 레벨 테스트 문제 전체를 조회합니다.
                    
                    - 로그인 여부와 관계없이 접근 가능합니다.
                    - 각 문제에는 선택지와 정답 정보가 포함됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "레벨 테스트 문제 조회 성공")
    })
    public List<LevelTest> getLevelTests() {
        return levelTestRepository.findAll();
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = """
                    이메일과 비밀번호로 로그인합니다.
                    
                    - 로그인 성공 시 세션에 LOGIN_USER_ID가 저장됩니다.
                    - 이후 인증이 필요한 API는 해당 세션을 기준으로 동작합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않음")
    })
    public LoginResponse login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        LoginResponse res = authService.login(request);

        HttpSession session = httpRequest.getSession();
        session.setAttribute(LOGIN_USER_ID, res.getId());

        return res;
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = """
                    현재 로그인된 사용자의 세션을 무효화하여 로그아웃 처리합니다.
                    
                    - 세션이 존재하지 않아도 에러 없이 성공 처리됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "로그인 상태 확인",
            description = """
                    현재 로그인된 사용자의 ID를 조회합니다.
                    
                    - 세션이 없거나 로그인 상태가 아니면 null을 반환합니다.
                    - 프론트에서 로그인 여부 판단용으로 사용합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
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

    @GetMapping("/me/profile")
    @Operation(
            summary = "내 프로필 조회",
            description = """
                    현재 로그인한 사용자의 프로필 정보를 조회합니다.
                    
                    - 닉네임
                    - 이메일
                    - 레벨
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 상태가 아님"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @Parameter(hidden = true) @CurrentUser Long userId
    ) {
        return ResponseEntity.ok(authService.getProfile(userId));
    }

    @PatchMapping("/me/profile")
    @Operation(
            summary = "내 프로필 수정",
            description = """
                    현재 로그인한 사용자의 프로필 정보를 수정합니다.
                    
                    - 수정 가능한 항목: 닉네임, 이메일
                    - 전달되지 않은 필드는 변경되지 않습니다.
                    - 닉네임/이메일 중복 시 오류가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "로그인 상태가 아님"),
            @ApiResponse(responseCode = "409", description = "닉네임 또는 이메일 중복")
    })
    public ResponseEntity<Void> updateProfile(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Valid @RequestBody UserMeUpdateDto dto
    ) {
        authService.updateProfile(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    @Operation(
            summary = "비밀번호 변경",
            description = """
                    현재 로그인한 사용자의 비밀번호를 변경합니다.
                    
                    - 현재 비밀번호가 일치해야 합니다.
                    - 새 비밀번호는 비밀번호 정책을 만족해야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 정책 위반 또는 확인 비밀번호 불일치"),
            @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치 또는 로그인 상태가 아님")
    })
    public ResponseEntity<Void> updatePassword(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Valid @RequestBody UserPasswordUpdateDto dto
    ) {
        authService.updatePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/withdraw")
    @Operation(
            summary = "회원 탈퇴",
            description = """
                    현재 로그인한 사용자의 계정을 삭제합니다.
                    
                    - 관련된 사용자 데이터도 함께 삭제됩니다.
                    - 탈퇴 후 세션은 자동으로 무효화됩니다.
                    """
    )
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
