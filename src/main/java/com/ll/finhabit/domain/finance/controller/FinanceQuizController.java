package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.FinanceQuizResponseDto;
import com.ll.finhabit.domain.finance.service.FinanceQuizService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "FinanceQuiz",
        description = "오늘의 금융 지식 카드 + 퀴즈를 한 번에 제공하는 통합 API"
)
@RestController
@RequestMapping("/api/finance-quiz")
@RequiredArgsConstructor
public class FinanceQuizController {

    private final FinanceQuizService financeQuizService;

    @GetMapping
    @Operation(
            summary = "오늘의 금융 지식 & 퀴즈 통합 조회",
            description = """
                    메인 화면 또는 자산 탭에서 사용하는 통합 API입니다.
                    
                    하나의 API 호출로 다음 데이터를 동시에 반환합니다.
                    - 오늘의 금융 지식 카드
                    - 해당 지식과 연동된 퀴즈 문제
                    
                    동작 규칙:
                    - 지식 카드가 아직 오픈되지 않은 경우, 먼저 오늘의 지식을 자동으로 배정합니다.
                    - 이미 오늘의 지식이 오픈된 경우, 기존 데이터를 그대로 반환합니다.
                    - 퀴즈는 항상 해당 날짜의 지식 카드 기준으로 제공됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "오늘의 금융 지식 및 퀴즈 데이터 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자 (인증 필요)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 오늘의 금융 지식/퀴즈 데이터를 찾을 수 없음"
            )
    })
    public ResponseEntity<FinanceQuizResponseDto> getFinanceQuizData(
            @Parameter(
                    hidden = true,
                    description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        FinanceQuizResponseDto dto = financeQuizService.getFinanceQuizData(userId);
        return ResponseEntity.ok(dto);
    }
}
