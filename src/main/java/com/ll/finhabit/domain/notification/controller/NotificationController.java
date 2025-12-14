package com.ll.finhabit.domain.notification.controller;

import com.ll.finhabit.domain.notification.dto.FeedbackNotificationRequest;
import com.ll.finhabit.domain.notification.dto.NotificationResponse;
import com.ll.finhabit.domain.notification.dto.NotificationSettingResponse;
import com.ll.finhabit.domain.notification.service.NotificationService;
import com.ll.finhabit.domain.notification.service.NotificationSettingService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 카드 API (미션/학습/피드백) + 알림 전체 토글")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationSettingService notificationSettingService;

    @Operation(
            summary = "미션 알림 카드 조회",
            description = "오늘 미션 완료 여부를 조회하여 알림 카드 1개를 반환합니다. (저장/히스토리 없음)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 알림 카드 반환 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/mission")
    public NotificationResponse mission(
            @CurrentUser @io.swagger.v3.oas.annotations.Parameter(hidden = true) Long userId
    ) {
        return notificationService.getMissionCard(userId);
    }

    @Operation(
            summary = "학습(금융지식) 알림 카드 조회",
            description = "DailyFinance(오늘/최신 카드)를 기반으로 학습 알림 카드 1개를 반환합니다. (저장/히스토리 없음)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "학습 알림 카드 반환 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/finance")
    public NotificationResponse finance(
            @CurrentUser @Parameter(hidden = true) Long userId
    ) {
        return notificationService.getLearningCard(userId);
    }

    @Operation(
            summary = "피드백 알림 카드 생성(계산) 후 반환",
            description = "요청 바디(trigger 등)를 기반으로 피드백 알림 카드 1개를 생성(계산)해서 반환합니다. (저장/히스토리 없음)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "피드백 알림 카드 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/feedback")
    public NotificationResponse feedback(
            @CurrentUser @Parameter(hidden = true) Long userId,
            @RequestBody FeedbackNotificationRequest request
    ) {
        return notificationService.getFeedbackCard(userId, request);
    }

    @PatchMapping("/settings/toggle")
    public NotificationSettingResponse toggle(
            @CurrentUser @Parameter(hidden = true) Long userId
    ) {
        return notificationSettingService.toggle(userId);
    }

}
