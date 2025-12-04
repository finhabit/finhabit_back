package com.ll.finhabit.domain.finance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.finhabit.domain.finance.dto.FinanceDto;
import com.ll.finhabit.domain.finance.service.DailyFinanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final DailyFinanceService dailyFinanceService;

    // 1. 오늘의 금융 지식 조회: GET /api/finance/today
    @GetMapping("/today")
    public ResponseEntity<FinanceDto> getTodayKnowledge() {
        FinanceDto knowledge = dailyFinanceService.getTodayFinanceKnowledge();
        if (knowledge != null) {
            return ResponseEntity.ok(knowledge);
        }
        return ResponseEntity.notFound().build();
    }

    // 2. 리스트 조회: GET /api/finance/week
    @GetMapping("/week")
    public ResponseEntity<List<FinanceDto>> getWeeklyKnowledge() {
        List<FinanceDto> list = dailyFinanceService.getWeeklyFinanceKnowledge();
        return ResponseEntity.ok(list);
    }

    // 3. 리스트 조회: GET /api/finance/month
    @GetMapping("/month")
    public ResponseEntity<List<FinanceDto>> getMonthlyKnowledge() {
        List<FinanceDto> list = dailyFinanceService.getMonthlyFinanceKnowledge();
        return ResponseEntity.ok(list);
    }

    // 4. 전체 조회: GET /api/finance
    @GetMapping
    public ResponseEntity<List<FinanceDto>> getAllKnowledge() {
        List<FinanceDto> list = dailyFinanceService.getAllFinanceKnowledge();
        return ResponseEntity.ok(list);
    }
}