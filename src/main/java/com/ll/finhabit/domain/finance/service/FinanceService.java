package com.ll.finhabit.domain.finance.service;

import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.entity.UserKnowledge;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;
import com.ll.finhabit.domain.finance.repository.UserKnowledgeRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final DailyFinanceRepository dailyFinanceRepository;

    @Transactional(readOnly = true)
    public List<DailyFinance> getAllDailyFinanceCards() {
        return dailyFinanceRepository.findAll();
    }

    private final UserKnowledgeRepository userKnowledgeRepository;
    private final UserRepository userRepository;

    @Transactional
    public FinanceCardDto getTodayFinanceKnowledge(Long userId) {
        LocalDate today = LocalDate.now();

        // 오늘 이미 오픈된 지식이 있는지 확인
        List<UserKnowledge> allUserKnowledge = userKnowledgeRepository.findByUserId(userId);
        Optional<UserKnowledge> existingTodayKnowledge =
                allUserKnowledge.stream()
                        .filter(uk -> uk.getOpendDate().isEqual(today))
                        .findFirst();

        if (existingTodayKnowledge.isPresent()) {
            // 이미 오늘 배정된 지식이 있다면, 해당 지식을 그대로 반환합니다.
            UserKnowledge uk = existingTodayKnowledge.get();
            DailyFinance dailyFinance =
                    dailyFinanceRepository
                            .findByFinanceId(uk.getFinanceId())
                            .orElseThrow(() -> new IllegalStateException("배정된 지식 콘텐츠를 찾을 수 없습니다."));

            // 조회 시간 업데이트
            uk.setViewedAt(today);
            userKnowledgeRepository.save(uk);

            return new FinanceCardDto(dailyFinance, uk);
        }

        // 오늘 배정된 지식이 없다면, 신규 배정 로직 시작
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "사용자 정보를 찾을 수 없습니다. (ID: " + userId + ")"));

        Integer userLevel = user.getLevel();

        List<DailyFinance> allFinance =
                dailyFinanceRepository.findByCardLevelOrderByCreatedDateAsc(userLevel);

        // 오늘 오픈할 새로운 지식 선정
        DailyFinance todayFinance =
                allFinance.stream()
                        .filter(
                                finance ->
                                        allUserKnowledge.stream()
                                                .noneMatch(
                                                        uk ->
                                                                uk.getFinanceId()
                                                                        .equals(
                                                                                finance
                                                                                        .getFinanceId())))
                        .findFirst()
                        .orElse(null);

        UserKnowledge knowledgeRecord;

        if (todayFinance != null) {
            // 2-2-A. 새로운 지식 오픈 (DB에 기록)
            knowledgeRecord = new UserKnowledge();
            knowledgeRecord.setUserId(userId);
            knowledgeRecord.setFinanceId(todayFinance.getFinanceId());
            knowledgeRecord.setOpendDate(today);
            knowledgeRecord.setViewedAt(today);
            userKnowledgeRepository.save(knowledgeRecord);

            return new FinanceCardDto(todayFinance, knowledgeRecord);

        } else {
            // 해당 레벨의 모든 지식이 이미 오픈된 경우: 가장 최근 카드 재조회

            knowledgeRecord =
                    allUserKnowledge.stream()
                            .max(Comparator.comparing(UserKnowledge::getOpendDate))
                            .orElseThrow(() -> new IllegalStateException("해당 레벨의 지식이 존재하지 않습니다."));

            DailyFinance recentlyViewedFinance =
                    dailyFinanceRepository
                            .findByFinanceId(knowledgeRecord.getFinanceId())
                            .orElseThrow(() -> new IllegalStateException("지식 콘텐츠를 찾을 수 없습니다."));

            // 조회 시간만 업데이트
            knowledgeRecord.setViewedAt(today);
            userKnowledgeRepository.save(knowledgeRecord);

            return new FinanceCardDto(recentlyViewedFinance, knowledgeRecord);
        }
    }

    public List<FinanceCardDto> getWeeklyKnowledge(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.with(DayOfWeek.MONDAY);

        return getKnowledgeByPeriod(userId, startDate, endDate);
    }

    public List<FinanceCardDto> getMonthlyKnowledge(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1);

        return getKnowledgeByPeriod(userId, startDate, endDate);
    }

    private List<FinanceCardDto> getKnowledgeByPeriod(
            Long userId, LocalDate startDate, LocalDate endDate) {
        List<UserKnowledge> knowledgeList =
                userKnowledgeRepository.findByUserIdAndOpendDateBetween(userId, startDate, endDate);

        return knowledgeList.stream()
                .map(
                        knowledge -> {
                            DailyFinance finance =
                                    dailyFinanceRepository
                                            .findByFinanceId(knowledge.getFinanceId())
                                            .orElse(null);
                            if (finance != null) {
                                return new FinanceCardDto(finance, knowledge);
                            }
                            return null;
                        })
                .filter(dto -> dto != null)
                .sorted(Comparator.comparing(FinanceCardDto::getOpendDate).reversed())
                .collect(Collectors.toList());
    }
}
