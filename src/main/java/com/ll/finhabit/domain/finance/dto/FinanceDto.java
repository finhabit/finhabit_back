package com.ll.finhabit.domain.finance.dto;

import java.time.LocalDate;

import com.ll.finhabit.domain.finance.entity.DailyFinance;

import lombok.Getter;

@Getter
public class FinanceDto {
    // DailyFinance 필드
    private String cardContent;
    private String cardTitle;
    private LocalDate date;
    private String cardLevel;

    // Quiz 정보에서 필요한 필드만 포함 (순환 참조 방지)
    private String question;

    // Entity -> DTO 변환 생성자
    public FinanceDto(DailyFinance entity) {
        this.cardContent = entity.getCardContent();
        this.cardTitle = entity.getCardTitle();
        this.date = entity.getDate();
        this.cardLevel = entity.getCardLevel();

        // DTO에 Quiz 엔티티(순환 참조 대상) 자체를 포함하지 않고,
        // 필요한 필드(질문)만 추출합니다.
        if (entity.getQuiz() != null) {
            this.question = entity.getQuiz().getQuestion();
        }
    }
}
