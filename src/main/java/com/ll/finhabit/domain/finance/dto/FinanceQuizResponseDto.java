package com.ll.finhabit.domain.finance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinanceQuizResponseDto {
    private final FinanceCardDto todayFinance;
    private final QuizQuestionDto todayQuiz;
}
