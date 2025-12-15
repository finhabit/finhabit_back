package com.ll.finhabit.domain.finance.dto;

import com.ll.finhabit.domain.finance.entity.Quiz;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizQuestionDto {
    private final Integer quizId;
    private final String question;
    private final String option1;
    private final String option2;
    private final String option3;

    private final Boolean isAnswered;

    public static QuizQuestionDto of(Quiz quiz, Boolean isAnswered) {
        return QuizQuestionDto.builder()
                .quizId(quiz.getQuizId())
                .question(quiz.getQuestion())
                .option1(quiz.getOption1())
                .option2(quiz.getOption2())
                .option3(quiz.getOption3())
                .isAnswered(isAnswered)
                .build();
    }
}
