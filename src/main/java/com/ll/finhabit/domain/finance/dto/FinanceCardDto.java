package com.ll.finhabit.domain.finance.dto;

import java.time.LocalDate;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.entity.UserKnowledge;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceCardDto {
    private Integer financeId;
    private String cardTitle;
    private String cardContent;
    private Integer cardLevel;
    private LocalDate opendDate;

    public FinanceCardDto(DailyFinance finance, UserKnowledge knowledge) {
        this.financeId = finance.getFinanceId();
        this.cardTitle = finance.getCardTitle();
        this.cardContent = finance.getCardContent();
        this.cardLevel = finance.getCardLevel();
        if (knowledge != null) {
            this.opendDate = knowledge.getOpendDate();
        }
    }
}
