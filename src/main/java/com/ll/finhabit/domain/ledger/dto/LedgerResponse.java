package com.ll.finhabit.domain.ledger.dto;

import com.ll.finhabit.domain.ledger.entity.Ledger;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class LedgerResponse {

    private Long ledgerId;
    private Long categoryId;
    private String categoryName;
    private String type;        // 수입/지출

    private Integer amount;
    private String memo;
    private String merchant;
    private LocalDate date;
    private String payment;

    public static LedgerResponse from(Ledger ledger) {
        return LedgerResponse.builder()
                .ledgerId(ledger.getLedgerId())
                .categoryId(ledger.getCategory().getCategoryId())
                .categoryName(ledger.getCategory().getCategoryName())
                .type(ledger.getCategory().getType())
                .amount(ledger.getAmount())
                .memo(ledger.getMemo())
                .merchant(ledger.getMerchant())
                .date(ledger.getDate())
                .payment(ledger.getPayment())
                .build();
    }
}
