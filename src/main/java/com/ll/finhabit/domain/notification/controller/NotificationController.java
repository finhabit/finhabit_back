package com.ll.finhabit.domain.notification.controller;

import com.ll.finhabit.domain.notification.dto.FeedbackNotificationRequest;
import com.ll.finhabit.domain.notification.dto.NotificationResponse;
import com.ll.finhabit.domain.notification.service.NotificationService;
import com.ll.finhabit.global.common.CurrentUser;
import com.ll.finhabit.domain.auth.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Notification",
        description = "알림센터 API (미션 / 학습 / 피드백 알림 관리)"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "알림센터 조회",
            description = "로그인한 사용자의 알림(미션/학습/피드백)을 최신순으로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping
    public List<NotificationResponse> list(@CurrentUser User user) {
        return notificationService.getMyNotifications(user.getId());
    }

    @Operation(
            summary = "미션 알림 생성",
            description = "오늘의 미션 상태를 기반으로 미션 알림을 생성하고 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 알림 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/mission")
    public NotificationResponse mission(@CurrentUser User user) {
        return notificationService.createMissionNotification(user.getId());
    }

    @Operation(
            summary = "학습 알림 생성",
            description = "오늘의 금융 지식을 기반으로 학습 알림을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "학습 알림 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/finance")
    public NotificationResponse learning(@CurrentUser User user) {
        return notificationService.createLearningNotification(user.getId());
    }

    @Operation(
            summary = "피드백 알림 생성",
            description = """
                    소비 분석 결과(주간/월간 리포트, 소비 급증, 미션 스트릭 등)를 기반으로
                    사용자 맞춤 피드백 알림을 생성합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "피드백 알림 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/feedback")
    public NotificationResponse feedback(
            @CurrentUser User user,
            @RequestBody FeedbackNotificationRequest request
    ) {
        return notificationService.createFeedbackNotification(user.getId(), request);
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "선택한 알림을 읽음 상태로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "403", description = "본인 알림이 아님"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PatchMapping("/{notificationId}/read")
    public void read(
            @CurrentUser User user,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(user.getId(), notificationId);
    }
}
