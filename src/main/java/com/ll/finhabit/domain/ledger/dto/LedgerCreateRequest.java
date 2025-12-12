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
public class LedgerCreateRequest {

    private Long categoryId;   // 카테고리 ID (필수)
    private Integer amount;    // 금액 (필수)
    private String memo;       // 메모 (선택)
    private String merchant;   // 사용처 (필수)
    private LocalDate date;    // 사용일 (필수)
    private String payment;    // 지불 방식 (필수, 예: "C", "H")
}
