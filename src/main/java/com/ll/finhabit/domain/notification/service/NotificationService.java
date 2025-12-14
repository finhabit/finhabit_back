package com.ll.finhabit.domain.notification.service;

import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;
import com.ll.finhabit.domain.notification.entity.NotificationType;
import com.ll.finhabit.domain.notification.dto.FeedbackNotificationRequest;
import com.ll.finhabit.domain.notification.dto.NotificationResponse;
import com.ll.finhabit.domain.notification.entity.Notification;
import com.ll.finhabit.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DailyFinanceRepository dailyFinanceRepository;

    // 알림센터 리스트
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 미션 알림 생성
    public NotificationResponse createMissionNotification(Long userId) {
        // TODO: 실제 미션 수행 여부/완료 여부 붙이면 여기서 문구 분기
        Notification n = Notification.builder()
                .userId(userId)
                .type(NotificationType.MISSION)
                .title("오늘도 성공!")
                .message("Finhabit이 당신의 습관을 응원해요 💚")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(notificationRepository.save(n));
    }

    // 학습(금융지식) 알림 생성 (DailyFinance 참고해서 “알림”으로 저장)
    public NotificationResponse createLearningNotification(Long userId) {
        DailyFinance finance = dailyFinanceRepository
                .findTopByCreatedDateOrderByIdDesc(LocalDate.now())
                .orElseGet(() -> dailyFinanceRepository.findTopByOrderByCreatedDateDescIdDesc().orElse(null));

        String title = (finance == null || isBlank(finance.getCardTitle()))
                ? "오늘의 금융 지식이 도착했어요!"
                : finance.getCardTitle();

        String message = (finance == null || isBlank(finance.getCardContent()))
                ? "‘신용점수 관리법’ 한 번 볼까요?"
                : finance.getCardContent();

        Notification n = Notification.builder()
                .userId(userId)
                .type(NotificationType.LEARNING)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(notificationRepository.save(n));
    }

    // 피드백 알림 생성 (표 예시 기반)
    public NotificationResponse createFeedbackNotification(Long userId, FeedbackNotificationRequest req) {
        String trigger = safeUpper(req.getTrigger());
        String target = safe(req.getTargetName(), "지출");

        String title;
        String message;

        switch (trigger) {
            case "WEEKLY_REPORT" -> { // 주간 리포트
                title = "주간 리포트";
                message = "📊 이번 주 " + target + " 지출이 " + formatPercent(req.getPercentDiff()) + " 대단해요 👏";
            }
            case "MONTHLY_REPORT" -> { // 월간 리포트
                title = "월간 리포트";
                message = "지난달보다 " + target + "가 " + formatAmount(req.getAmountDiff()) + " 멋진 변화예요! ⭐";
            }
            case "SPENDING_SPIKE" -> { // 소비 급증
                title = "소비 급증 알림";
                message = "⚠️ 이번 주 " + target + "가 지난주보다 " + formatPercent(req.getPercentDiff()) + " 계획 점검 어떠요?";
            }
            case "MISSION_STREAK" -> { // 미션 꾸준히 수행
                title = "미션 스트릭";
                int streak = req.getStreakDays() == null ? 0 : req.getStreakDays();
                message = "🔥 Finhabit streak " + streak + "일째! 완벽한 습관 관리네요.";
            }
            case "LONG_INACTIVE" -> { // 장기 미접속
                title = "복귀 알림";
                message = "😊 오랜만이에요! Finhabit과 함께 다시 금융 루틴 시작해볼까요?";
            }
            default -> {
                title = "피드백 알림";
                message = "지난달보다 " + target + " 변화가 있어요. 이번 주 패턴을 한번 점검해볼까요?";
            }
        }

        Notification n = Notification.builder()
                .userId(userId)
                .type(NotificationType.FEEDBACK)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(notificationRepository.save(n));
    }

    // 읽음 처리
    public void markAsRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("notification not found"));

        if (!n.getUserId().equals(userId)) {
            throw new IllegalArgumentException("forbidden");
        }
        n.markRead();
    }

    // mapping
    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    // helpers
    private String safeUpper(String v) {
        return v == null ? "" : v.trim().toUpperCase();
    }

    private String safe(String v, String def) {
        if (v == null) return def;
        String t = v.trim();
        return t.isEmpty() ? def : t;
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    // diff: -15 => "15% 줄었어요." / 30 => "30% 늘었어요."
    private String formatPercent(Integer diff) {
        if (diff == null) return "변화했어요.";
        if (diff > 0) return diff + "% 늘었어요.";
        if (diff < 0) return Math.abs(diff) + "% 줄었어요.";
        return "변동이 없어요.";
    }

    // diff: -20000 => "20,000원 감소했어요." / 20000 => "20,000원 증가했어요."
    private String formatAmount(Integer diff) {
        if (diff == null) return "변화했어요.";
        int abs = Math.abs(diff);
        String won = String.format("%,d원", abs);
        if (diff > 0) return won + " 증가했어요.";
        if (diff < 0) return won + " 감소했어요.";
        return "변동이 없어요.";
    }
}
