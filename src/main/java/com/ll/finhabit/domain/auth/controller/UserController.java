package com.ll.finhabit.domain.auth.controller;

import com.ll.finhabit.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final AuthService authService;
    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    @DeleteMapping("/me")
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
