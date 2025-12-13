package com.ll.finhabit.domain.ledger.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LedgerHomeResponse {

    private LocalDate date; // 기준 날짜 (보통 오늘)
    private TodaySpending today; // 오늘의 소비 카드
    private List<TodayCategory> todayCategories; // 오늘 카테고리별 소비 카드들
    private MonthSummary monthSummary; // 월간 소비 요약(도넛)

    @Getter
    @Builder
    public static class TodaySpending {
        private int totalExpense; // 오늘 총 지출
        private List<LedgerResponse> ledgers; // 오늘 지출 내역 전체 리스트
    }

    @Getter
    @Builder
    public static class TodayCategory {
        private Long categoryId;
        private String categoryName;
        private int amount; // 해당 카테고리 지출 합계
        private int percent; // 오늘 지출 중 비율 (%)
    }

    @Getter
    @Builder
    public static class MonthSummary {
        private int year;
        private int month;
        private int totalExpense;
        private List<CategoryRatio> categories; // 도넛용 카테고리 비율 리스트
    }

    @Getter
    @Builder
    public static class CategoryRatio {
        private Long categoryId;
        private String categoryName;
        private int amount;
        private int percent;
    }
}
