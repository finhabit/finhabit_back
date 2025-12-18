package com.ll.finhabit.domain.auth.service;

import com.ll.finhabit.domain.auth.dto.LevelTestAnswer;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.entity.UserLevel;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.repository.UserLevelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LevelTestGrader {

    private final LevelTestRepository levelTestRepository;
    private final UserLevelRepository userLevelRepository;

    private static final int TOTAL_QUESTIONS = 5;

    public GradeResult gradeAndSave(User user, List<LevelTestAnswer> answers) {
        if (answers == null || answers.isEmpty()) {
            return GradeResult.empty(TOTAL_QUESTIONS);
        }

        int correctCount = 0;

        for (LevelTestAnswer answerDto : answers) {
            LevelTest test =
                    levelTestRepository
                            .findById(answerDto.getTestId())
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.BAD_REQUEST, "존재하지 않는 문제입니다."));

            boolean isCorrect = test.getTestAnswer().equals(answerDto.getUserAnswer());
            if (isCorrect) correctCount++;

            UserLevel userLevel =
                    UserLevel.builder()
                            .user(user)
                            .test(test)
                            .isCorrect(isCorrect)
                            .userAnswer(answerDto.getUserAnswer())
                            .build();

            userLevelRepository.save(userLevel);
        }

        int level = calculateLevel(correctCount);
        int correctRate = (int) Math.round(correctCount * 100.0 / TOTAL_QUESTIONS);

        return new GradeResult(correctCount, correctRate, level);
    }

    private int calculateLevel(int correctCount) {
        if (correctCount >= 4) return 3;
        if (correctCount >= 2) return 2;
        return 1;
    }

    public record GradeResult(int correctCount, int correctRate, int level) {
        static GradeResult empty(int totalQuestions) {
            return new GradeResult(0, 0, 1);
        }
    }
}
