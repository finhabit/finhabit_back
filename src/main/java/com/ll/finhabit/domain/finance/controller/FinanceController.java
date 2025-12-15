package com.ll.finhabit.domain.finance.controller;

import com.ll.finhabit.domain.finance.dto.FinanceCardDto;
import com.ll.finhabit.domain.finance.entity.DailyFinance;
import com.ll.finhabit.domain.finance.service.FinanceService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Finance", description = "금융 지식 카드 제공 및 사용자 열람 기록 관리 API")
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping
    @Operation(
            summary = "오늘의 금융 지식 카드 조회",
            description = """
                    로그인된 사용자의 레벨에 맞는 금융 지식 카드를 하루에 1회 제공합니다.
                    
                    - 하루에 최초 호출 시: 새로운 카드가 오픈 및 저장됩니다.
                    - 같은 날 재호출 시: 이미 오픈된 카드가 그대로 반환됩니다.
                    - 카드 오픈 여부는 사용자별로 관리됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "오늘의 금융 지식 카드 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 해당 레벨의 금융 지식을 찾을 수 없음")
    })
    public ResponseEntity<FinanceCardDto> getTodayFinanceKnowledge(
            @Parameter(
                    hidden = true,
                    description = "세션을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        FinanceCardDto dto = financeService.getTodayFinanceKnowledge(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/week")
    @Operation(
            summary = "주간 금융 지식 모아보기",
            description = """
                    로그인된 사용자가 이번 주에 오픈한 금융 지식 카드 목록을 조회합니다.
                    
                    - 조회 범위: 이번 주 월요일 00:00부터 오늘까지
                    - 정렬 기준: 최신 오픈 날짜 순 (내림차순)
                    - 오늘의 카드도 포함됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주간 금융 지식 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자")
    })
    public ResponseEntity<List<FinanceCardDto>> getWeeklyKnowledge(
            @Parameter(
                    hidden = true,
                    description = "세션을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        List<FinanceCardDto> dtoList = financeService.getWeeklyKnowledge(userId);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/month")
    @Operation(
            summary = "월간 금융 지식 모아보기",
            description = """
                    로그인된 사용자가 이번 달에 오픈한 금융 지식 카드 목록을 조회합니다.
                    
                    - 조회 범위: 이번 달 1일 00:00부터 오늘까지
                    - 정렬 기준: 최신 오픈 날짜 순 (내림차순)
                    - 사용자의 열람 기록을 기준으로 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "월간 금융 지식 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자")
    })
    public ResponseEntity<List<FinanceCardDto>> getMonthlyKnowledge(
            @Parameter(
                    hidden = true,
                    description = "세션을 통해 자동 주입되는 로그인 사용자 ID"
            )
            @CurrentUser Long userId
    ) {

        List<FinanceCardDto> dtoList = financeService.getMonthlyKnowledge(userId);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/all")
    @Operation(
            summary = "⚠️ [개발/테스트용] 전체 금융 지식 카드 조회",
            description = """
                    DB에 저장된 모든 금융 지식 카드(DailyFinance)를 조회합니다.
                    
                    - 인증 없이 접근 가능합니다.
                    - 사용자 레벨, 날짜, 오픈 여부와 관계없이 전체 데이터를 반환합니다.
                    - 운영 환경에서는 사용을 권장하지 않습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 금융 지식 카드 조회 성공")
    })
    public ResponseEntity<List<DailyFinance>> getAllFinanceKnowledge() {

        List<DailyFinance> allCards = financeService.getAllDailyFinanceCards();
        return ResponseEntity.ok(allCards);
    }
}
