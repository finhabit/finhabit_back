package com.ll.finhabit.domain.finance.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.entity.UserKnowledge;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;
import com.ll.finhabit.domain.finance.repository.UserKnowledgeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final DailyFinanceRepository dailyFinanceRepository;
    private final UserKnowledgeRepository userKnowledgeRepository;
    private final UserRepository userRepository;

    /**
     * GET /api/finance : 오늘의 금융 지식 조회 및 오픈 처리
     */
    @Transactional
    public FinanceCardDto getTodayFinanceKnowledge(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다. (ID: " + userId + ")"));

        // ✨ User의 level (Integer)을 그대로 사용
        Integer userLevel = user.getLevel();

        // DailyFinanceRepository 호출 시 Integer 타입 전달
        List<DailyFinance> allFinance = dailyFinanceRepository.findByCardLevelOrderByCreatedDateAsc(userLevel);
        List<UserKnowledge> openedKnowledge = userKnowledgeRepository.findByUserId(userId);

        // 오늘 오픈할 지식 선정
        DailyFinance todayFinance = allFinance.stream()
                .filter(finance -> openedKnowledge.stream()
                        .noneMatch(uk -> uk.getFinanceId().equals(finance.getFinanceId())))
                .findFirst()
                .orElse(null);

        UserKnowledge knowledgeRecord;
        LocalDate today = LocalDate.now();

        if (todayFinance != null) {
            // 새로운 지식 오픈
            knowledgeRecord = new UserKnowledge();
            knowledgeRecord.setUserId(userId);
            knowledgeRecord.setFinanceId(todayFinance.getFinanceId());
            knowledgeRecord.setOpendDate(today);
            knowledgeRecord.setViewedAt(today);
            userKnowledgeRepository.save(knowledgeRecord);

        } else {
            // 이미 모든 지식 오픈됨: 가장 최근에 오픈한 지식을 보여줌
            knowledgeRecord = openedKnowledge.stream()
                    .max(Comparator.comparing(UserKnowledge::getOpendDate))
                    .orElseThrow(() -> new IllegalStateException("해당 레벨의 지식이 존재하지 않습니다."));

            todayFinance = dailyFinanceRepository.findByFinanceId(knowledgeRecord.getFinanceId())
                    .orElseThrow(() -> new IllegalStateException("지식 콘텐츠를 찾을 수 없습니다."));

            knowledgeRecord.setViewedAt(today);
            userKnowledgeRepository.save(knowledgeRecord);
        }

        return new FinanceCardDto(todayFinance, knowledgeRecord);
    }

    /**
     * GET /api/finance/week : 주간 모아보기
     */
    public List<FinanceCardDto> getWeeklyKnowledge(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);

        return getKnowledgeByPeriod(userId, startDate, endDate);
    }

    /**
     * GET /api/finance/month : 월간 모아보기
     */
    public List<FinanceCardDto> getMonthlyKnowledge(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.withDayOfMonth(1);

        return getKnowledgeByPeriod(userId, startDate, endDate);
    }

    private List<FinanceCardDto> getKnowledgeByPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        List<UserKnowledge> knowledgeList = userKnowledgeRepository.findByUserIdAndOpendDateBetween(userId, startDate, endDate);

        return knowledgeList.stream()
                .map(knowledge -> {
                    DailyFinance finance = dailyFinanceRepository.findByFinanceId(knowledge.getFinanceId()).orElse(null);
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