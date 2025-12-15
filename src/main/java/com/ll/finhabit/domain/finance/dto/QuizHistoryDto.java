package com.ll.finhabit.domain.finance.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizHistoryDto {
    private final String monthLabel;
    private final List<WeeklyQuizHistory> weeklyHistory;

    @Getter
    @Builder
    public static class WeeklyQuizHistory {
        private final String weekLabel;
        private final List<QuizHistoryItem> quizzes;
    }

    @Getter
    @Builder
    public static class QuizHistoryItem {
        private final Integer quizId;
        private final LocalDate attemptedDate;
        private final String cardTitle;
        private final Boolean isCorrect;
    }
}
