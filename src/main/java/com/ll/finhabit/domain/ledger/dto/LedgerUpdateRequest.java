package com.ll.finhabit.domain.ledger.dto;

import com.ll.finhabit.domain.ledger.entity.PaymentType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerUpdateRequest {

    private Long categoryId; // 변경할 카테고리 ID
    private Integer amount;
    private String merchant;
    private LocalDate date;
    private PaymentType payment;
}
