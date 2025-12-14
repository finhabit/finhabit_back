package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.service.FinanceService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Finance", description = "금융 지식 (카드) 및 사용자 열람 기록 관리 API")
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @Operation(
            summary = "오늘의 금융 지식 조회 및 오픈",
            description = "로그인된 사용자의 레벨에 맞는 새로운 지식 카드를 하루에 1회 배정합니다. 당일 재호출 시에는 배정된 카드를 다시 반환합니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 카드 조회 및 배정"),
                @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
                @ApiResponse(responseCode = "404", description = "사용자 또는 콘텐츠를 찾을 수 없음")
            })
    @GetMapping
    public ResponseEntity<FinanceCardDto> getTodayFinanceKnowledge(
            // ArgumentResolver로 주입되며, Swagger UI에서 숨깁니다.
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        FinanceCardDto dto = financeService.getTodayFinanceKnowledge(userId);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "주간 모아보기 (이번 주 월요일부터 오늘까지)",
            description = "로그인된 사용자가 이번 주(월요일 시작)에 오픈했던 모든 지식 카드 목록을 반환합니다. 가장 최근 날짜 순으로 정렬됩니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 주간 기록 조회"),
                @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
            })
    @GetMapping("/week")
    public ResponseEntity<List<FinanceCardDto>> getWeeklyKnowledge(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        List<FinanceCardDto> dtoList = financeService.getWeeklyKnowledge(userId);
        return ResponseEntity.ok(dtoList);
    }

    @Operation(
            summary = "월간 모아보기 (이번 달 1일부터 오늘까지)",
            description = "로그인된 사용자가 이번 달에 오픈했던 모든 지식 카드 목록을 반환합니다. 가장 최근 날짜 순으로 정렬됩니다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "성공적인 월간 기록 조회"),
                @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
            })
    @GetMapping("/month")
    public ResponseEntity<List<FinanceCardDto>> getMonthlyKnowledge(
            @Parameter(hidden = true, description = "세션에서 자동 주입되는 사용자 ID") @CurrentUser
                    Long userId) {

        List<FinanceCardDto> dtoList = financeService.getMonthlyKnowledge(userId);
        return ResponseEntity.ok(dtoList);
    }

    @Operation(
            summary = "⚠️ [개발/확인용] 전체 금융 지식 카드 조회",
            description = "인증 없이(로그인 없이) DB에 저장된 모든 DailyFinance 카드를 Level 구분 없이 반환합니다. (테스트 용도)",
            responses = {@ApiResponse(responseCode = "200", description = "전체 목록 반환 성공")})
    @GetMapping("/all")
    public ResponseEntity<List<DailyFinance>> getAllFinanceKnowledge() {
        List<DailyFinance> allCards = financeService.getAllDailyFinanceCards();
        return ResponseEntity.ok(allCards);
    }
}
