package com.ll.finhabit.domain.finance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizCheckDto {
    private final Integer quizId;
    private final Boolean isCorrect;
    private final Integer selectedAnswer;
}
