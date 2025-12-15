package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.*;
import com.ll.finhabit.domain.finance.service.QuizService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Quiz", description = "오늘의 퀴즈 조회 및 정답 체크 API")
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @Operation(
            summary = "GET /api/quiz/today: 오늘의 퀴즈 문제 조회",
            description = "오늘 오픈된 금융 지식 카드와 연동된 퀴즈를 반환합니다. 하루 1회만 배정되며, 이후 호출 시 이미 배정된 퀴즈를 반환합니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 퀴즈 문제 조회"),
                @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                @ApiResponse(responseCode = "404", description = "오늘의 퀴즈 또는 지식 카드를 찾을 수 없음")
            })
    @GetMapping("/today")
    public ResponseEntity<QuizQuestionDto> getTodayQuiz(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        QuizQuestionDto dto = quizService.getTodayQuiz(userId);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "POST /api/quiz/check: 퀴즈 정답 체크",
            description = "사용자가 선택한 답변을 제출하여 정답 여부를 확인하고 기록합니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 정답 체크"),
                @ApiResponse(responseCode = "400", description = "이미 퀴즈를 풀었거나, 요청 데이터 오류"),
                @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    @PostMapping("/check")
    public ResponseEntity<QuizCheckDto> checkQuizAnswer(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser Long userId,
            @RequestBody QuizRequestDto requestDto) {

        QuizCheckDto dto = quizService.checkQuizAnswer(userId, requestDto);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "GET /api/quiz/today/answer: 퀴즈 정답 및 해설 조회",
            description = "오늘 푼 퀴즈의 정답, 해설, 사용자의 정답 여부를 조회합니다. (정답 체크 후 호출 가능)",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 해설 조회"),
                @ApiResponse(responseCode = "400", description = "정답을 체크하지 않아 해설을 볼 수 없음"),
                @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    @GetMapping("/today/answer")
    public ResponseEntity<QuizAnswerDto> getQuizAnswer(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        QuizAnswerDto dto = quizService.getQuizAnswer(userId);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "GET /api/quiz/list: 지난 퀴즈 조회 (월별/주차별 그룹화)",
            description = "이번 달을 기준으로 주차별(1주차, 2주차, 현재 주차 등)로 푼 퀴즈 목록을 그룹화하여 반환합니다. (미래 주차는 조회 X)",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 지난 퀴즈 목록 조회"),
                @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    @GetMapping("/list")
    public ResponseEntity<QuizHistoryDto> getQuizHistory(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        QuizHistoryDto dto = quizService.getQuizHistory(userId);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "GET /api/quiz/{quizId}: 지난 퀴즈 상세 조회",
            description =
                    "특정 quizId에 대해 연결된 지식 카드, 퀴즈 문제, 해설 및 사용자 응시 기록을 통합하여 반환합니다. (지난 퀴즈 목록 클릭 시 사용)",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 상세 정보 조회"),
                @ApiResponse(responseCode = "400", description = "해당 퀴즈에 대한 응시 기록이 없거나 퀴즈 정보 부족"),
                @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizHistoryDetailDto> getQuizHistoryDetail(
            @Parameter(hidden = true) @CurrentUser Long userId, @PathVariable Integer quizId) {

        QuizHistoryDetailDto dto = quizService.getQuizHistoryDetail(userId, quizId);
        return ResponseEntity.ok(dto);
    }
}
