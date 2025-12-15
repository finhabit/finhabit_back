package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.FinanceQuizResponseDto;
import com.ll.finhabit.domain.finance.service.FinanceQuizService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FinanceQuiz", description = "금융 지식 및 퀴즈 통합 데이터 API")
@RestController
@RequestMapping("/api/finance-quiz")
@RequiredArgsConstructor
public class FinanceQuizController {

    private final FinanceQuizService financeQuizService;

    @Operation(
            summary = "오늘의 금융 지식 및 퀴즈 통합 조회 (메인/자산 탭용)",
            description = "하나의 호출로 오늘의 지식 카드와 퀴즈 문제를 동시에 반환하며, 퀴즈 연동 전 지식 배정을 보장합니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 데이터 통합 조회"),
                @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
            })
    @GetMapping
    public ResponseEntity<FinanceQuizResponseDto> getFinanceQuizData(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        FinanceQuizResponseDto dto = financeQuizService.getFinanceQuizData(userId);
        return ResponseEntity.ok(dto);
    }
}
