package com.ll.finhabit.domain.finance.service;

import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.dto.FinanceQuizResponseDto;
import com.ll.finhabit.domain.finance.dto.QuizQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinanceQuizService {

    private final FinanceService financeService;
    private final QuizService quizService;

    @Transactional
    public FinanceQuizResponseDto getFinanceQuizData(Long userId) {

        FinanceCardDto todayFinance = financeService.getTodayFinanceKnowledge(userId);
        QuizQuestionDto todayQuiz = quizService.getTodayQuiz(userId);

        return FinanceQuizResponseDto.builder()
                .todayFinance(todayFinance)
                .todayQuiz(todayQuiz)
                .build();
    }
}
