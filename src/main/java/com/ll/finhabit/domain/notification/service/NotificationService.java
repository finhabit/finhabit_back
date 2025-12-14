package com.ll.finhabit.domain.notification.service;

import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;
import com.ll.finhabit.domain.mission.entity.UserMission;
import com.ll.finhabit.domain.mission.repository.UserMissionRepository;
import com.ll.finhabit.domain.notification.dto.FeedbackNotificationRequest;
import com.ll.finhabit.domain.notification.dto.NotificationResponse;
import com.ll.finhabit.domain.notification.entity.NotificationSetting;
import com.ll.finhabit.domain.notification.entity.NotificationType;
import com.ll.finhabit.domain.notification.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final DailyFinanceRepository dailyFinanceRepository;
    private final UserMissionRepository userMissionRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationResponse getMissionCard(Long userId) {
        if (!isNotificationEnabled(userId)) return offCard();

        LocalDate today = LocalDate.now();

        UserMission todayMission = userMissionRepository
                .findByUser_IdAndAssignedDate(userId, today)
                .orElse(null);

        boolean completed =
                todayMission != null && Boolean.TRUE.equals(todayMission.getIsCompleted());

        if (completed) {
            return NotificationResponse.builder()
                    .type(NotificationType.MISSION)
                    .title("👏 오늘도 성공!")
                    .message("Finhabit이 당신의 습관을 응원해요 💰")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return NotificationResponse.builder()
                .type(NotificationType.MISSION)
                .title("미션 리마인드")
                .message("오늘의 미션이 아직 남았어요 😅 한 번만 더 도전해볼까요?")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public NotificationResponse getLearningCard(Long userId) {
        if (!isNotificationEnabled(userId)) return offCard();

        DailyFinance finance = dailyFinanceRepository
                .findTopByCreatedDateOrderByIdDesc(LocalDate.now())
                .orElseGet(() -> dailyFinanceRepository.findTopByOrderByCreatedDateDescIdDesc().orElse(null));

        String title = (finance == null || isBlank(finance.getCardTitle()))
                ? "오늘의 금융 지식이 도착했어요!"
                : finance.getCardTitle();

        String message = (finance == null || isBlank(finance.getCardContent()))
                ? "오늘의 금융 지식이 아직 준비되지 않았어요."
                : finance.getCardContent();

        return NotificationResponse.builder()
                .type(NotificationType.LEARNING)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public NotificationResponse getFeedbackCard(Long userId, FeedbackNotificationRequest req) {
        if (!isNotificationEnabled(userId)) return offCard();

        String trigger = req.getTrigger() == null ? "" : req.getTrigger().trim().toUpperCase();
        String target = (req.getTargetName() == null || req.getTargetName().trim().isEmpty())
                ? "지출"
                : req.getTargetName().trim();

        String title;
        String message;

        switch (trigger) {
            case "WEEKLY_REPORT" -> {
                title = "주간 리포트";
                message = "📊 이번 주 " + target + " 지출이 " + formatPercent(req.getPercentDiff()) + " 대단해요 👏";
            }
            case "MONTHLY_REPORT" -> {
                title = "월간 리포트";
                message = "지난달보다 " + target + "가 " + formatAmount(req.getAmountDiff()) + " 멋진 변화예요! ⭐";
            }
            case "SPENDING_SPIKE" -> {
                title = "소비 급증 알림";
                message = "⚠️ 이번 주 " + target + "가 지난주보다 " + formatPercent(req.getPercentDiff()) + " 계획 점검 어떠요?";
            }
            case "MISSION_STREAK" -> {
                title = "미션 스트릭";
                int streak = req.getStreakDays() == null ? 0 : req.getStreakDays();
                message = "🔥 Finhabit streak " + streak + "일째! 완벽한 습관 관리네요.";
            }
            case "LONG_INACTIVE" -> {
                title = "복귀 알림";
                message = "😊 오랜만이에요! Finhabit과 함께 다시 금융 루틴 시작해볼까요?";
            }
            default -> {
                title = "피드백 알림";
                message = "지난달보다 " + target + " 변화가 있어요. 이번 주 패턴을 한번 점검해볼까요?";
            }
        }

        return NotificationResponse.builder()
                .type(NotificationType.FEEDBACK)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /* ===== helpers ===== */

    private boolean isNotificationEnabled(Long userId) {
        return notificationSettingRepository.findByUserId(userId)
                .map(NotificationSetting::isEnabled)
                .orElse(true); // 설정 없으면 기본 ON
    }

    private NotificationResponse offCard() {
        return NotificationResponse.builder()
                .type(null) // 필요하면 NotificationType.FEEDBACK 같은 값으로 고정해도 됨
                .title("알림이 꺼져있어요")
                .message("설정에서 알림을 켜면 다시 받을 수 있어요.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private String formatPercent(Integer diff) {
        if (diff == null) return "변화했어요.";
        if (diff > 0) return diff + "% 늘었어요.";
        if (diff < 0) return Math.abs(diff) + "% 줄었어요.";
        return "변동이 없어요.";
    }

    private String formatAmount(Integer diff) {
        if (diff == null) return "변화했어요.";
        int abs = Math.abs(diff);
        String won = String.format("%,d원", abs);
        if (diff > 0) return won + " 증가했어요.";
        if (diff < 0) return won + " 감소했어요.";
        return "변동이 없어요.";
    }
}
