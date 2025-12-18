package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.FinanceQuizResponseDto;
import com.ll.finhabit.domain.finance.service.FinanceQuizService;
import com.ll.finhabit.global.common.CurrentUser;
import com.ll.finhabit.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FinanceQuiz", description = "오늘의 금융 지식 카드 + 퀴즈를 한 번에 제공하는 통합 API")
@RestController
@RequestMapping("/api/finance-quiz")
@RequiredArgsConstructor
public class FinanceQuizController {

    private final FinanceQuizService financeQuizService;

    @GetMapping
    @Operation(
            summary = "오늘의 금융 지식 & 퀴즈 통합 조회",
            description =
                    """
                    메인 화면 또는 자산 탭에서 사용하는 통합 API입니다.

                    하나의 API 호출로 다음 데이터를 동시에 반환합니다.
                    - 오늘의 금융 지식 카드
                    - 해당 지식과 연동된 퀴즈 문제

                    동작 규칙:
                    - 지식 카드가 아직 오픈되지 않은 경우, 먼저 오늘의 지식을 자동으로 배정합니다.
                    - 이미 오늘의 지식이 오픈된 경우, 기존 데이터를 그대로 반환합니다.
                    - 퀴즈는 항상 해당 날짜의 지식 카드 기준으로 제공됩니다.
                    """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "오늘의 금융 지식 및 퀴즈 데이터 조회 성공",
                content =
                        @Content(schema = @Schema(implementation = FinanceQuizResponseDto.class))),
        @ApiResponse(
                responseCode = "400",
                description = "오늘의 금융 지식/퀴즈 데이터를 찾을 수 없거나 배정 실패",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples = {
                                    @ExampleObject(
                                            name = "오늘 오픈된 금융 지식 카드 없음",
                                            value =
                                                    """
                                        {
                                          "timestamp": "2025-12-18T17:30:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "오늘 오픈된 금융 지식 카드가 없습니다.",
                                          "path": "/api/finance-quiz"
                                        }
                                        """),
                                    @ExampleObject(
                                            name = "연결된 퀴즈 없음",
                                            value =
                                                    """
                                        {
                                          "timestamp": "2025-12-18T17:30:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "오늘의 금융 지식에 연결된 퀴즈가 없습니다.",
                                          "path":  "/api/finance-quiz"
                                        }
                                        """),
                                    @ExampleObject(
                                            name = "배정된 지식 콘텐츠 없음",
                                            value =
                                                    """
                                        {
                                          "timestamp": "2025-12-18T17:30:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "배정된 지식 콘텐츠를 찾을 수 없습니다.",
                                          "path": "/api/finance-quiz"
                                        }
                                        """),
                                    @ExampleObject(
                                            name = "해당 레벨의 지식 없음",
                                            value =
                                                    """
                                        {
                                          "timestamp": "2025-12-18T17:30:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "해당 레벨의 지식이 존재하지 않습니다.",
                                          "path":  "/api/finance-quiz"
                                        }
                                        """),
                                    @ExampleObject(
                                            name = "사용자 정보 없음",
                                            value =
                                                    """
                                        {
                                          "timestamp": "2025-12-18T17:30:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "사용자 정보를 찾을 수 없습니다.",
                                          "path": "/api/finance-quiz"
                                        }
                                        """)
                                })),
        @ApiResponse(
                responseCode = "401",
                description = "로그인되지 않은 사용자 (인증 필요)",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        """
                                {
                                  "timestamp": "2025-12-18T17:30:00",
                                  "status": 401,
                                  "error": "401 UNAUTHORIZED",
                                  "message": "로그인이 필요합니다.",
                                  "path": "/api/finance-quiz"
                                }
                                """)))
    })
    public ResponseEntity<FinanceQuizResponseDto> getFinanceQuizData(
            @Parameter(hidden = true, description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID") @CurrentUser
                    Long userId) {

        FinanceQuizResponseDto dto = financeQuizService.getFinanceQuizData(userId);
        return ResponseEntity.ok(dto);
    }
}
