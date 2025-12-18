package com.ll.finhabit.domain.notification.controller;

import com.ll.finhabit.domain.notification.dto.NotificationResponse;
import com.ll.finhabit.domain.notification.dto.NotificationSettingResponse;
import com.ll.finhabit.domain.notification.service.NotificationService;
import com.ll.finhabit.domain.notification.service.NotificationSettingService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification", description = "알림 카드 API (미션/학습/피드백) + 알림 전체 토글")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    @Operation(
            summary = "미션 알림 카드 조회",
            description = "오늘 미션 완료 여부를 조회하여 알림 카드 1개를 반환합니다.   (저장/히스토리 없음)")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "미션 알림 카드 반환 성공",
                content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
        @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
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
                                  "status":  401,
                                  "error": "401 UNAUTHORIZED",
                                  "message": "로그인이 필요합니다.",
                                  "path": "/api/notifications/mission"
                                }
                                """))),
        @ApiResponse(
                responseCode = "404",
                description = "사용자 또는 미션을 찾을 수 없음",
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
                                  "status": 404,
                                  "error": "404 NOT_FOUND",
                                  "message": "사용자를 찾을 수 없습니다.",
                                  "path": "/api/notifications/mission"
                                }
                                """)))
    })
    @GetMapping("/mission")
    public NotificationResponse mission(@CurrentUser @Parameter(hidden = true) Long userId) {
        return notificationService.getMissionCard(userId);
    }

    @Operation(
            summary = "학습(금융지식) 알림 카드 조회",
            description = "DailyFinance(오늘/최신 카드)를 기반으로 학습 알림 카드 1개를 반환합니다.  (저장/히스토리 없음)")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "학습 알림 카드 반환 성공",
                content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
        @ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자",
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
                                  "path": "/api/notifications/finance"
                                }
                                """))),
        @ApiResponse(
                responseCode = "404",
                description = "금융 지식 카드를 찾을 수 없음",
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
                                  "status": 404,
                                  "error": "404 NOT_FOUND",
                                  "message": "금융 지식 카드를 찾을 수 없습니다.",
                                  "path": "/api/notifications/finance"
                                }
                                """)))
    })
    @GetMapping("/finance")
    public NotificationResponse finance(@Parameter(hidden = true) @CurrentUser Long userId) {
        return notificationService.getLearningCard(userId);
    }

    @Operation(
            summary = "피드백 알림 카드 조회",
            description = "서버가 가계부(Ledger) 데이터를 분석해 오늘의 피드백 카드 1개를 반환합니다.  (요청 바디 없음)")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "피드백 카드 반환 성공",
                content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
        @ApiResponse(
                responseCode = "401",
                description = "로그인이 필요합니다",
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
                                  "message":  "로그인이 필요합니다.",
                                  "path": "/api/notifications/feedback"
                                }
                                """))),
        @ApiResponse(
                responseCode = "404",
                description = "사용자 또는 가계부 데이터를 찾을 수 없음",
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
                                  "status": 404,
                                  "error": "404 NOT_FOUND",
                                  "message":  "가계부 데이터를 찾을 수 없습니다.",
                                  "path": "/api/notifications/feedback"
                                }
                                """)))
    })
    @GetMapping("/feedback")
    public NotificationResponse feedback(@Parameter(hidden = true) @CurrentUser Long userId) {
        return notificationService.getFeedbackCard(userId);
    }

    @Operation(
            summary = "알림 전체 ON/OFF 토글",
            description =
                    """
                사용자의 알림 설정을 한 번의 요청으로 전체 ON/OFF 합니다.

                - 미션 알림
                - 학습(금융지식) 알림
                - 피드백 알림

                토글 방식으로 동작하며, 현재 상태가 ON이면 OFF로,
                OFF이면 ON으로 변경됩니다.
                """)
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "알림 설정 토글 성공",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        NotificationSettingResponse.class))),
        @ApiResponse(
                responseCode = "401",
                description = "로그인이 필요합니다",
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
                                  "path": "/api/notifications/settings/toggle"
                                }
                                """))),
        @ApiResponse(
                responseCode = "404",
                description = "알림 설정을 찾을 수 없음",
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
                                  "status":  404,
                                  "error": "404 NOT_FOUND",
                                  "message": "알림 설정을 찾을 수 없습니다.",
                                  "path": "/api/notifications/settings/toggle"
                                }
                                """)))
    })
    @PatchMapping("/settings/toggle")
    public NotificationSettingResponse toggle(
            @CurrentUser @Parameter(hidden = true, description = "로그인한 사용자 ID (세션 기반)")
                    Long userId) {
        return notificationSettingService.toggle(userId);
    }
}
