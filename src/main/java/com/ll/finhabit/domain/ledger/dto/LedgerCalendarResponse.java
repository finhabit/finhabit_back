package com.ll.finhabit.domain.ledger.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LedgerCalendarResponse {

    private LocalDate date; // 선택된 날짜
    private int totalIncome; // 그 날 수입 총합
    private int totalExpense; // 그 날 지출 총합

    // 도넛 차트용 : 카테고리별 비율 (지출 기준)
    private List<LedgerHomeResponse.CategoryRatio> categories;

    // 아래 리스트용 : 그 날의 전체 내역
    private List<LedgerResponse> ledgers;
}
