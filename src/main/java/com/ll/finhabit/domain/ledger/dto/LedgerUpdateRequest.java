package com.ll.finhabit.domain.ledger.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerUpdateRequest {

    private Long categoryId;   // 변경할 카테고리 ID
    private Integer amount;
    private String memo;
    private String merchant;
    private LocalDate date;
    private String payment;
}
