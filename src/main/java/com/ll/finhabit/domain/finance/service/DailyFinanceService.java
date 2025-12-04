package com.ll.finhabit.domain.finance.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ll.finhabit.domain.finance.dto.FinanceDto;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.repository.DailyFinanceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DailyFinanceService {
    private final DailyFinanceRepository dailyFinanceRepository;

    // 1. 오늘의 금융 지식 조회: /api/finance/today
    public FinanceDto getTodayFinanceKnowledge() {
        LocalDate today = LocalDate.now();
        // Repository가 Entity를 가져옴 (Lazy Loading으로 Quiz는 당장 로드되지 않을 수 있음)
        DailyFinance entity = dailyFinanceRepository.findByDate(today);

        if (entity != null) {
            return new FinanceDto(entity); // DTO로 변환
        }
        return null;
    }

    // 2-1. 주간 지식 카드 목록 조회: /api/finance/week
    public List<FinanceDto> getWeeklyFinanceKnowledge() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<DailyFinance> entities = dailyFinanceRepository.findAllByDateBetween(startDate, endDate);

        // List<Entity>를 List<DTO>로 변환하여 반환
        return entities.stream()
                .map(FinanceDto::new)
                .collect(Collectors.toList());
    }

    // 2-2. 월간 지식 카드 목록 조회: /api/finance/month
    public List<FinanceDto> getMonthlyFinanceKnowledge() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.with(TemporalAdjusters.lastDayOfMonth());

        List<DailyFinance> entities = dailyFinanceRepository.findAllByDateBetween(startDate, endDate);

        return entities.stream()
                .map(FinanceDto::new)
                .collect(Collectors.toList());
    }

    // 3. 전체 저장된 지식 카드 리스트 조회: /api/finance
    public List<FinanceDto> getAllFinanceKnowledge() {
        List<DailyFinance> entities = dailyFinanceRepository.findAll();

        return entities.stream()
                .map(FinanceDto::new)
                .collect(Collectors.toList());
    }
}