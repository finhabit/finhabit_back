package com.ll.finhabit.domain.finance.service;

import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.dto.QuizAnswerDto;
import com.ll.finhabit.domain.finance.dto.QuizCheckDto;
import com.ll.finhabit.domain.finance.dto.QuizHistoryDetailDto;
import com.ll.finhabit.domain.finance.dto.QuizHistoryDto;
import com.ll.finhabit.domain.finance.dto.QuizQuestionDto;
import com.ll.finhabit.domain.finance.dto.QuizRequestDto;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.entity.Quiz;
import com.ll.finhabit.domain.finance.entity.UserKnowledge;
import com.ll.finhabit.domain.finance.entity.UserQuiz;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;
import com.ll.finhabit.domain.finance.repository.QuizRepository;
import com.ll.finhabit.domain.finance.repository.UserKnowledgeRepository;
import com.ll.finhabit.domain.finance.repository.UserQuizRepository;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    private final DailyFinanceRepository dailyFinanceRepository;
    private final UserKnowledgeRepository userKnowledgeRepository; // 추가

    /** 오늘의 퀴즈를 조회하고, 아직 풀지 않았다면 배정합니다. */
    @Transactional
    public QuizQuestionDto getTodayQuiz(Long userId) {
        LocalDate today = LocalDate.now();

        Optional<UserQuiz> existingUserQuizOpt =
                userQuizRepository.findByUserIdAndAttemptedDate(userId, today);

        if (existingUserQuizOpt.isPresent()) {
            UserQuiz existingUserQuiz = existingUserQuizOpt.get();
            Quiz quiz =
                    quizRepository
                            .findByQuizId(existingUserQuiz.getQuizId())
                            .orElseThrow(() -> new IllegalArgumentException("퀴즈 정보를 찾을 수 없습니다."));
            return QuizQuestionDto.of(quiz, existingUserQuiz.getIsAnswered());
        }

        // 2. 오늘 오픈된 금융 지식 카드의 퀴즈 ID를 가져옵니다.
        UserKnowledge todayKnowledge =
                userKnowledgeRepository
                        .findByUserIdAndOpendDate(userId, today) // 수정 완료
                        .orElseThrow(() -> new IllegalStateException("오늘 오픈된 금융 지식 카드가 없습니다."));

        DailyFinance todayFinance =
                dailyFinanceRepository
                        .findByFinanceId(todayKnowledge.getFinanceId())
                        .orElseThrow(() -> new IllegalStateException("DailyFinance를 찾을 수 없습니다."));

        Integer quizId = todayFinance.getQuizId();

        if (quizId == null) {
            throw new IllegalStateException("오늘의 금융 지식에 연결된 퀴즈가 없습니다.");
        }

        // 3. 퀴즈 문제 조회 및 사용자 기록 생성
        Quiz quiz =
                quizRepository
                        .findByQuizId(quizId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "퀴즈 정보를 찾을 수 없습니다. (ID: " + quizId + ")"));

        UserQuiz userQuiz = new UserQuiz();
        userQuiz.setUserId(userId);
        userQuiz.setQuizId(quizId);
        userQuiz.setAttemptedDate(today);
        userQuizRepository.save(userQuiz);

        return QuizQuestionDto.of(quiz, false);
    }

    /** 사용자가 제출한 정답을 체크하고 기록합니다. (포인트 로직 제거) */
    @Transactional
    public QuizCheckDto checkQuizAnswer(Long userId, QuizRequestDto requestDto) {
        LocalDate today = LocalDate.now();

        UserQuiz userQuiz =
                userQuizRepository
                        .findByUserIdAndAttemptedDate(userId, today)
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "오늘의 퀴즈 기록이 없습니다. 먼저 퀴즈를 조회해야 합니다."));

        if (userQuiz.getIsAnswered()) {
            throw new IllegalStateException("이미 퀴즈를 풀었습니다.");
        }

        Quiz quiz =
                quizRepository
                        .findByQuizId(requestDto.getQuizId())
                        .orElseThrow(() -> new IllegalArgumentException("퀴즈 정보를 찾을 수 없습니다."));

        // 2. 정답 체크
        boolean isCorrect = quiz.getAnswer().equals(requestDto.getSelectedAnswer());

        // 3. 사용자 기록 업데이트
        userQuiz.setIsAnswered(true);
        userQuiz.setIsCorrect(isCorrect);
        userQuiz.setSelectedAnswer(requestDto.getSelectedAnswer());
        userQuizRepository.save(userQuiz);

        // 4. 포인트 관련 로직 완전히 제거

        return QuizCheckDto.builder()
                .quizId(quiz.getQuizId())
                .isCorrect(isCorrect)
                .selectedAnswer(requestDto.getSelectedAnswer())
                // earnedPoint 필드 제거 완료
                .build();
    }

    /** 퀴즈 정답 및 해설을 조회합니다. */
    @Transactional(readOnly = true)
    public QuizAnswerDto getQuizAnswer(Long userId) {
        LocalDate today = LocalDate.now();

        UserQuiz userQuiz =
                userQuizRepository
                        .findByUserIdAndAttemptedDate(userId, today)
                        .orElseThrow(() -> new IllegalStateException("오늘의 퀴즈 기록이 없습니다."));

        if (!userQuiz.getIsAnswered()) {
            throw new IllegalStateException("정답을 체크하지 않아 해설을 볼 수 없습니다.");
        }

        Quiz quiz =
                quizRepository
                        .findByQuizId(userQuiz.getQuizId())
                        .orElseThrow(() -> new IllegalArgumentException("퀴즈 정보를 찾을 수 없습니다."));

        return QuizAnswerDto.of(quiz, userQuiz.getIsCorrect());
    }

    /** 지난 퀴즈 조회: 이번 달을 기준으로 주차별로 그룹화하여 반환합니다. */
    @Transactional(readOnly = true)
    public QuizHistoryDto getQuizHistory(Long userId) {
        List<UserQuiz> allUserQuizzes =
                userQuizRepository.findByUserIdOrderByAttemptedDateDesc(userId);

        if (allUserQuizzes.isEmpty()) {
            return QuizHistoryDto.builder()
                    .monthLabel(
                            LocalDate.now().getYear()
                                    + "년 "
                                    + LocalDate.now().getMonthValue()
                                    + "월")
                    .weeklyHistory(Collections.emptyList())
                    .build();
        }

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        List<UserQuiz> currentMonthQuizzes =
                allUserQuizzes.stream()
                        .filter(
                                uq ->
                                        uq.getAttemptedDate().getMonthValue() == currentMonth
                                                && uq.getAttemptedDate().getYear() == currentYear)
                        .collect(Collectors.toList());

        Locale locale = Locale.KOREA;
        WeekFields weekFields = WeekFields.of(locale);

        Map<Integer, List<UserQuiz>> quizzesByWeek =
                currentMonthQuizzes.stream()
                        .collect(
                                Collectors.groupingBy(
                                        uq -> uq.getAttemptedDate().get(weekFields.weekOfMonth())));

        List<QuizHistoryDto.WeeklyQuizHistory> weeklyHistory = new ArrayList<>();

        int currentWeekOfMonth = now.get(weekFields.weekOfMonth());

        for (int week = 1; week <= currentWeekOfMonth; week++) {
            List<UserQuiz> weeklyQuizzes =
                    quizzesByWeek.getOrDefault(week, Collections.emptyList());

            List<QuizHistoryDto.QuizHistoryItem> items =
                    weeklyQuizzes.stream()
                            .filter(UserQuiz::getIsAnswered)
                            .map(
                                    uq -> {
                                        String cardTitle = getCardTitleByQuizId(uq.getQuizId());
                                        return QuizHistoryDto.QuizHistoryItem.builder()
                                                .quizId(uq.getQuizId())
                                                .attemptedDate(uq.getAttemptedDate())
                                                .cardTitle(cardTitle)
                                                .isCorrect(uq.getIsCorrect())
                                                .build();
                                    })
                            .sorted(
                                    Comparator.comparing(
                                                    QuizHistoryDto.QuizHistoryItem
                                                            ::getAttemptedDate)
                                            .reversed())
                            .collect(Collectors.toList());

            if (!items.isEmpty() || week == currentWeekOfMonth) {
                weeklyHistory.add(
                        QuizHistoryDto.WeeklyQuizHistory.builder()
                                .weekLabel(week + "주차")
                                .quizzes(items)
                                .build());
            }
        }

        weeklyHistory.sort(
                Comparator.comparing(QuizHistoryDto.WeeklyQuizHistory::getWeekLabel).reversed());

        return QuizHistoryDto.builder()
                .monthLabel(currentYear + "년 " + currentMonth + "월")
                .weeklyHistory(weeklyHistory)
                .build();
    }

    /** GET /api/quiz/{quizId} : 지난 퀴즈 상세 정보 조회 (지식 카드 + 퀴즈 + 해설 통합) */
    @Transactional(readOnly = true)
    public QuizHistoryDetailDto getQuizHistoryDetail(Long userId, Integer quizId) {

        // 1. UserQuiz 기록 조회 (사용자가 이 퀴즈를 풀었는지 확인)
        UserQuiz userQuiz =
                userQuizRepository.findByUserIdOrderByAttemptedDateDesc(userId).stream()
                        .filter(uq -> uq.getQuizId().equals(quizId))
                        .filter(UserQuiz::getIsAnswered) // 푼 기록만
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 퀴즈에 대한 사용자의 응시 기록을 찾을 수 없습니다."));

        // 2. Quiz 문제 및 해설 조회
        Quiz quiz =
                quizRepository
                        .findByQuizId(quizId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "퀴즈 정보를 찾을 수 없습니다. (ID: " + quizId + ")"));

        // 3. DailyFinance 정보 조회 및 FinanceCardDto 생성
        DailyFinance dailyFinance =
                dailyFinanceRepository
                        .findByQuizId(quizId)
                        .orElseThrow(
                                () -> new IllegalStateException("퀴즈와 연결된 금융 지식 카드를 찾을 수 없습니다."));

        // 4. UserKnowledge 정보 조회 (지식 카드 DTO 생성을 위해 필요)
        UserKnowledge userKnowledge =
                userKnowledgeRepository.findByUserId(userId).stream()
                        .filter(uk -> uk.getFinanceId().equals(dailyFinance.getFinanceId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("지식 카드 열람 기록을 찾을 수 없습니다."));

        FinanceCardDto financeCardDto = new FinanceCardDto(dailyFinance, userKnowledge);

        // 5. Quiz Detail DTO 생성
        QuizHistoryDetailDto.QuizDetail quizDetail =
                QuizHistoryDetailDto.QuizDetail.builder()
                        .quizId(quizId)
                        .question(quiz.getQuestion())
                        .option1(quiz.getOption1())
                        .option2(quiz.getOption2())
                        .option3(quiz.getOption3())
                        .selectedAnswer(userQuiz.getSelectedAnswer())
                        .isCorrect(userQuiz.getIsCorrect())
                        .actualAnswer(quiz.getAnswer())
                        .explanation(quiz.getExplanation())
                        .build();

        // 6. 최종 통합 DTO 반환
        return QuizHistoryDetailDto.builder()
                .financeCard(financeCardDto)
                .quizDetail(quizDetail)
                .build();
    }

    private String getCardTitleByQuizId(Integer quizId) {
        return dailyFinanceRepository
                .findByQuizId(quizId)
                .map(DailyFinance::getCardTitle)
                .orElse("연결된 지식 카드 없음");
    }
}
