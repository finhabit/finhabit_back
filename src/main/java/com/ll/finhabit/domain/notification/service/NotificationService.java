package com.ll.finhabit.domain.notification.service;

import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;
import com.ll.finhabit.domain.ledger.entity.Ledger;
import com.ll.finhabit.domain.ledger.repository.LedgerRepository;
import com.ll.finhabit.domain.mission.entity.UserMission;
import com.ll.finhabit.domain.mission.repository.UserMissionRepository;
import com.ll.finhabit.domain.notification.dto.NotificationResponse;
import com.ll.finhabit.domain.notification.entity.NotificationSetting;
import com.ll.finhabit.domain.notification.entity.NotificationType;
import com.ll.finhabit.domain.notification.repository.NotificationSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final DailyFinanceRepository dailyFinanceRepository;
    private final UserMissionRepository userMissionRepository;
    private final LedgerRepository ledgerRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    public NotificationResponse getMissionCard(Long userId) {
        if (!isNotificationEnabled(userId)) return offCard(NotificationType.MISSION);

        LocalDate today = LocalDate.now();

        UserMission todayMission =
                userMissionRepository.findByUser_IdAndAssignedDate(userId, today).orElse(null);

        if (todayMission == null) {
            return NotificationResponse.builder()
                    .type(NotificationType.MISSION)
                    .title("오늘의 미션 도착!")
                    .message("오늘 실천할 작은 목표가 있어요 😊 지금 확인해볼까요?")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        Integer doneCount = todayMission.getDoneCount();
        if (doneCount != null && doneCount > 0) {
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
                .message("오늘의 미션이 아직 남았어요 😅 한 번 도전해볼까요?")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public NotificationResponse getLearningCard(Long userId) {
        if (!isNotificationEnabled(userId)) return offCard(NotificationType.LEARNING);

        DailyFinance finance =
                dailyFinanceRepository
                        .findTopByCreatedDateOrderByIdDesc(LocalDate.now())
                        .orElseGet(
                                () ->
                                        dailyFinanceRepository
                                                .findTopByOrderByCreatedDateDescIdDesc()
                                                .orElse(null));

        String title = "💡 오늘의 금융 지식이 도착했어요!";

        String message =
                (finance == null || isBlank(finance.getCardTitle()))
                        ? "오늘의 금융 지식이 아직 준비되지 않았어요."
                        : "‘" + finance.getCardTitle() + "’ 한 번 볼까요?";

        return NotificationResponse.builder()
                .type(NotificationType.LEARNING)
                .title(title)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public NotificationResponse getFeedbackCard(Long userId) {
        if (!isNotificationEnabled(userId)) return offCard(NotificationType.FEEDBACK);

        LocalDate today = LocalDate.now();

        // ====== 주간 범위 ======
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);
        LocalDate thisSunday = thisMonday.plusDays(6);

        LocalDate lastMonday = thisMonday.minusWeeks(1);
        LocalDate lastSunday = lastMonday.plusDays(6);

        List<Ledger> thisWeekLedgers =
                expenseLedgers(
                        ledgerRepository.findAllByUser_IdAndDateBetween(
                                userId, thisMonday, thisSunday));
        List<Ledger> lastWeekLedgers =
                expenseLedgers(
                        ledgerRepository.findAllByUser_IdAndDateBetween(
                                userId, lastMonday, lastSunday));

        int thisWeekTotal = sumAmount(thisWeekLedgers);
        int lastWeekTotal = sumAmount(lastWeekLedgers);

        /** 우선순위: 1) 소비 급증(카테고리) 2) 주간 리포트(총액) 3) 월간 리포트(총액) 4) 기본 */
        // 소비 급증
        SpikeResult spike = detectSpike(thisWeekLedgers, lastWeekLedgers);
        if (spike != null) {
            return NotificationResponse.builder()
                    .type(NotificationType.FEEDBACK)
                    .title("소비 급증 알림")
                    .message(
                            "⚠️ 이번 주 "
                                    + spike.categoryName
                                    + " 지출이 "
                                    + spike.diffText
                                    + " 계획 점검 어때요?")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        // 주간 리포트
        if (thisWeekTotal > 0 || lastWeekTotal > 0) {
            String msg = "📊 이번 주 총 지출이 " + formatPercentDiff(thisWeekTotal, lastWeekTotal) + "!";
            return NotificationResponse.builder()
                    .type(NotificationType.FEEDBACK)
                    .title("주간 리포트")
                    .message(msg)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        // 월간 범위(이번달: 1일~오늘, 지난달: 지난달 1일~동일 일자)
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);

        LocalDate endDayLastMonth = firstDayLastMonth.plusDays(today.getDayOfMonth() - 1L);
        LocalDate lastMonthLastDay =
                firstDayLastMonth.withDayOfMonth(firstDayLastMonth.lengthOfMonth());
        if (endDayLastMonth.isAfter(lastMonthLastDay)) endDayLastMonth = lastMonthLastDay;

        List<Ledger> thisMonthLedgers =
                expenseLedgers(
                        ledgerRepository.findAllByUser_IdAndDateBetween(
                                userId, firstDayThisMonth, today));
        List<Ledger> lastMonthLedgers =
                expenseLedgers(
                        ledgerRepository.findAllByUser_IdAndDateBetween(
                                userId, firstDayLastMonth, endDayLastMonth));

        int thisMonthTotal = sumAmount(thisMonthLedgers);
        int lastMonthTotal = sumAmount(lastMonthLedgers);

        if (thisMonthTotal > 0 || lastMonthTotal > 0) {
            int diff = thisMonthTotal - lastMonthTotal;
            String msg =
                    "지난달 대비 " + formatAmount(diff) + " (이번달 " + formatWon(thisMonthTotal) + ")";
            return NotificationResponse.builder()
                    .type(NotificationType.FEEDBACK)
                    .title("월간 리포트")
                    .message(msg)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        // 기본
        return NotificationResponse.builder()
                .type(NotificationType.FEEDBACK)
                .title("피드백 알림")
                .message("이번 주 소비 패턴을 한 번 점검해볼까요?")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // helpers
    private boolean isNotificationEnabled(Long userId) {
        return notificationSettingRepository
                .findByUserId(userId)
                .map(NotificationSetting::isEnabled)
                .orElse(true);
    }

    private NotificationResponse offCard(NotificationType type) {
        return NotificationResponse.builder()
                .type(type)
                .title("알림이 꺼져있어요")
                .message("설정에서 알림을 켜면 다시 받을 수 있어요.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private List<Ledger> expenseLedgers(List<Ledger> ledgers) {
        if (ledgers == null || ledgers.isEmpty()) return List.of();
        return ledgers.stream()
                .filter(l -> l.getCategory() != null)
                .filter(l -> isExpense(l.getCategory().getType()))
                .toList();
    }

    private boolean isExpense(String type) {
        if (type == null) return false;
        String t = type.trim().toLowerCase();
        return t.equals("expense") || t.equals("지출");
    }

    private int sumAmount(List<Ledger> ledgers) {
        return ledgers.stream().mapToInt(l -> l.getAmount() == null ? 0 : l.getAmount()).sum();
    }

    private Map<String, Integer> sumByCategory(List<Ledger> ledgers) {
        Map<String, Integer> map = new HashMap<>();
        for (Ledger l : ledgers) {
            String name =
                    (l.getCategory() == null || l.getCategory().getCategoryName() == null)
                            ? "기타"
                            : l.getCategory().getCategoryName();
            int amt = l.getAmount() == null ? 0 : l.getAmount();
            map.put(name, map.getOrDefault(name, 0) + amt);
        }
        return map;
    }

    private SpikeResult detectSpike(List<Ledger> thisWeek, List<Ledger> lastWeek) {
        Map<String, Integer> thisSum = sumByCategory(thisWeek);
        Map<String, Integer> lastSum = sumByCategory(lastWeek);

        SpikeResult best = null;

        for (Map.Entry<String, Integer> e : thisSum.entrySet()) {
            String cat = e.getKey();
            int cur = e.getValue();
            int prev = lastSum.getOrDefault(cat, 0);

            if (cur <= 0) continue;

            // 지난주 0 → 이번주 발생
            if (prev == 0 && cur >= 5000) {
                SpikeResult r = new SpikeResult(cat, "새로 " + formatWon(cur) + " 발생했어요.");
                if (best == null) best = r;
                continue;
            }

            int diff = cur - prev;
            if (diff <= 0) continue;

            int percent = (int) Math.round(diff * 100.0 / prev);
            if (percent >= 50 && diff >= 5000) {
                SpikeResult r = new SpikeResult(cat, percent + "% 늘었어요.");
                if (best == null) best = r;
            }
        }
        return best;
    }

    private String formatPercentDiff(int current, int previous) {
        if (previous == 0 && current > 0) return "처음으로 기록됐어요";
        if (previous == 0) return "변동이 없어요";
        int diff = current - previous;
        int percent = (int) Math.round(diff * 100.0 / previous);
        if (percent > 0) return percent + "% 늘었어요";
        if (percent < 0) return Math.abs(percent) + "% 줄었어요";
        return "변동이 없어요";
    }

    private String formatAmount(int diff) {
        if (diff > 0) return formatWon(diff) + " 증가했어요";
        if (diff < 0) return formatWon(-diff) + " 감소했어요";
        return "변동이 없어요";
    }

    private String formatWon(int amount) {
        return String.format("%,d원", amount);
    }

    private static class SpikeResult {
        final String categoryName;
        final String diffText;

        SpikeResult(String categoryName, String diffText) {
            this.categoryName = categoryName;
            this.diffText = diffText;
        }
    }
}
