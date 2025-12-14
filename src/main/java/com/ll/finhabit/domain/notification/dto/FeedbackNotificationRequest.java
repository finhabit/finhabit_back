package com.ll.finhabit.domain.notification.dto;

import lombok.Getter;

@Getter
public class FeedbackNotificationRequest {
    /**
     * WEEKLY_REPORT        : 주간 리포트
     * MONTHLY_REPORT       : 월간 리포트
     * SPENDING_SPIKE       : 소비 급증 시
     * MISSION_STREAK       : 미션 꾸준히 수행 중
     * LONG_INACTIVE        : 장기 미접속 시
     */
    private String trigger;

    // 예) "커피", "외식비" (없으면 기본 문구로 처리)
    private String targetName;

    // 예) -15면 15% 줄었어요 / +30이면 30% 늘었어요
    private Integer percentDiff;

    // 예) -20000면 2만 원 감소 / +20000이면 2만 원 증가
    private Integer amountDiff;

    // 예) 5 (streak 5일째)
    private Integer streakDays;
}
