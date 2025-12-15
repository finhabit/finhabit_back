package com.ll.finhabit.domain.finance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizHistoryDetailDto {
    private final FinanceCardDto financeCard;
    private final QuizDetail quizDetail;

    @Getter
    @Builder
    public static class QuizDetail {
        private final Integer quizId;
        private final String question;
        private final String option1;
        private final String option2;
        private final String option3;

        private final Integer selectedAnswer;
        private final Boolean isCorrect;
        private final Integer actualAnswer;
        private final String explanation;
    }
}
