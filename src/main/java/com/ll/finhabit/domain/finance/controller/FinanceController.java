package com.ll.finhabit.domain.finance.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.service.FinanceService;
import com.ll.finhabit.global.common.CurrentUser;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    /**
     * GET /api/finance : 오늘의 금융 지식 조회 및 오픈
     * @Parameter(hidden = true)를 통해 Swagger UI에서 이 파라미터 입력 필드를 숨깁니다.
     */
    @GetMapping
    public ResponseEntity<FinanceCardDto> getTodayFinanceKnowledge(
            @Parameter(hidden = true) @CurrentUser Long userId) { // ✨ 수정

        FinanceCardDto dto = financeService.getTodayFinanceKnowledge(userId);
        return ResponseEntity.ok(dto);
    }

    /**
     * GET /api/finance/week : 주간 모아보기
     */
    @GetMapping("/week")
    public ResponseEntity<List<FinanceCardDto>> getWeeklyKnowledge(
            @Parameter(hidden = true) @CurrentUser Long userId) { // ✨ 수정

        List<FinanceCardDto> dtoList = financeService.getWeeklyKnowledge(userId);
        return ResponseEntity.ok(dtoList);
    }

    /**
     * GET /api/finance/month : 월간 모아보기
     */
    @GetMapping("/month")
    public ResponseEntity<List<FinanceCardDto>> getMonthlyKnowledge(
            @Parameter(hidden = true) @CurrentUser Long userId) { // ✨ 수정

        List<FinanceCardDto> dtoList = financeService.getMonthlyKnowledge(userId);
        return ResponseEntity.ok(dtoList);
    }
}