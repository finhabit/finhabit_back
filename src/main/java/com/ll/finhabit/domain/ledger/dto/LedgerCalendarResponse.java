package com.ll.finhabit.domain.ledger.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LedgerCalendarResponse {

    private LocalDate date;
    private int totalIncome;
    private int totalExpense;

    private List<LedgerHomeResponse.CategoryRatio> categories;

    private List<LedgerResponse> ledgers;
}
