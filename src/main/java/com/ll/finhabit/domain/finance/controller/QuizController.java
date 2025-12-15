package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.QuizAnswerDto;
import com.ll.finhabit.domain.finance.dto.QuizCheckDto;
import com.ll.finhabit.domain.finance.dto.QuizHistoryDetailDto;
import com.ll.finhabit.domain.finance.dto.QuizHistoryDto;
import com.ll.finhabit.domain.finance.dto.QuizQuestionDto;
import com.ll.finhabit.domain.finance.dto.QuizRequestDto;
import com.ll.finhabit.domain.finance.service.QuizService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Quiz",
        description = "오늘의 퀴즈 조회, 정답 제출, 해설 및 지난 퀴즈 히스토리 조회 API"
)
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/today")
    @Operation(
            summary = "오늘의 퀴즈 문제 조회",
            description = """
                    오늘 오픈된 금융 지식 카드와 연동된 퀴즈 문제를 조회합니다.

                    동작 규칙:
                    - 하루에 1개의 퀴즈만 배정됩니다.
                    - 최초 호출 시 오늘의 퀴즈가 배정됩니다.
                    - 이후 재호출 시 이미 배정된 퀴즈 문제를 그대로 반환합니다.
                    - 퀴즈는 반드시 오늘의 금융 지식 카드와 연결됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "오늘의 퀴즈 문제 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자 (인증 필요)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "오늘의 퀴즈 또는 연결된 금융 지식 카드가 존재하지 않음"
            )
    })
    public ResponseEntity<QuizQuestionDto> getTodayQuiz(
            @Parameter(
                    hidden = true,
                    description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        QuizQuestionDto dto = quizService.getTodayQuiz(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/check")
    @Operation(
            summary = "퀴즈 정답 제출 및 채점",
            description = """
                    사용자가 선택한 퀴즈 답안을 제출하여 정답 여부를 판별합니다.

                    동작 규칙:
                    - 오늘의 퀴즈에 대해서만 제출할 수 있습니다.
                    - 이미 정답을 제출한 경우 다시 제출할 수 없습니다.
                    - 제출 결과로 정답 여부, 선택 답안 정보가 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "퀴즈 정답 채점 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 퀴즈를 푼 경우 또는 요청 데이터가 올바르지 않음"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자 (인증 필요)"
            )
    })
    public ResponseEntity<QuizCheckDto> checkQuizAnswer(
            @Parameter(
                    hidden = true,
                    description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId,
            @RequestBody QuizRequestDto requestDto
    ) {

        QuizCheckDto dto = quizService.checkQuizAnswer(userId, requestDto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/today/answer")
    @Operation(
            summary = "오늘의 퀴즈 정답 및 해설 조회",
            description = """
                    오늘 푼 퀴즈의 정답, 해설, 그리고 사용자의 정답 여부를 조회합니다.

                    제약 사항:
                    - 반드시 퀴즈 정답 제출(check)을 완료한 후에만 조회할 수 있습니다.
                    - 아직 정답을 제출하지 않은 경우 조회할 수 없습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "퀴즈 정답 및 해설 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "아직 퀴즈 정답을 제출하지 않아 해설을 볼 수 없음"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자 (인증 필요)"
            )
    })
    public ResponseEntity<QuizAnswerDto> getQuizAnswer(
            @Parameter(
                    hidden = true,
                    description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        QuizAnswerDto dto = quizService.getQuizAnswer(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/list")
    @Operation(
            summary = "지난 퀴즈 목록 조회 (주차별 그룹화)",
            description = """
                    사용자가 이번 달에 풀었던 퀴즈들을 주차별로 그룹화하여 반환합니다.

                    특징:
                    - 1주차, 2주차, 현재 주차 등으로 그룹화됩니다.
                    - 아직 도래하지 않은 미래 주차는 포함되지 않습니다.
                    - 각 항목은 지난 퀴즈 상세 조회 API와 연동됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지난 퀴즈 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자 (인증 필요)"
            )
    })
    public ResponseEntity<QuizHistoryDto> getQuizHistory(
            @Parameter(
                    hidden = true,
                    description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        QuizHistoryDto dto = quizService.getQuizHistory(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{quizId}")
    @Operation(
            summary = "지난 퀴즈 상세 조회",
            description = """
                    특정 quizId에 해당하는 퀴즈의 상세 정보를 조회합니다.

                    반환 데이터:
                    - 연결된 금융 지식 카드 정보
                    - 퀴즈 문제 및 보기
                    - 정답 및 해설
                    - 사용자의 선택 답안 및 정답 여부

                    사용 위치:
                    - 지난 퀴즈 목록 화면에서 개별 퀴즈 클릭 시
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지난 퀴즈 상세 정보 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "해당 퀴즈에 대한 응시 기록이 없거나 데이터가 불완전함"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인되지 않은 사용자 (인증 필요)"
            )
    })
    public ResponseEntity<QuizHistoryDetailDto> getQuizHistoryDetail(
            @Parameter(
                    hidden = true,
                    description = "세션 기반 인증을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId,
            @PathVariable Integer quizId
    ) {

        QuizHistoryDetailDto dto = quizService.getQuizHistoryDetail(userId, quizId);
        return ResponseEntity.ok(dto);
    }
}
