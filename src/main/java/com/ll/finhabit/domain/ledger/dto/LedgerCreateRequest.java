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
public class LedgerCreateRequest {

    private Long categoryId; // 카테고리 ID (필수)
    private Integer amount; // 금액 (필수)
    private String merchant; // 사용처 (필수)
    private LocalDate date; // 사용일 (필수)
    private PaymentType payment; // 지불 방식
}
