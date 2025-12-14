package com.ll.finhabit.domain.ledger.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LedgerResponse {

    private Long ledgerId;
    private Long categoryId;
    private String categoryName;
    private String type; // 수입/지출

    private Integer amount;
    private String merchant;
    private LocalDate date;
    private String payment; // CARD, CASH, ETC
}
