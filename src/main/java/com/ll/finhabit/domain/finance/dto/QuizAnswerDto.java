package com.ll.finhabit.domain.finance.dto;

import com.ll.finhabit.domain.finance.entity.Quiz;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizAnswerDto {
    private final Integer quizId;
    private final Integer answer;
    private final String explanation;
    private final Boolean isCorrect;

    public static QuizAnswerDto of(Quiz quiz, Boolean isCorrect) {
        return QuizAnswerDto.builder()
                .quizId(quiz.getQuizId())
                .answer(quiz.getAnswer())
                .explanation(quiz.getExplanation())
                .isCorrect(isCorrect)
                .build();
    }
}
