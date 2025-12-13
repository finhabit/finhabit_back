package com.ll.finhabit.domain.ledger.controller;

import com.ll.finhabit.domain.ledger.dto.*;
import com.ll.finhabit.domain.ledger.service.LedgerService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledger")
@Tag(name = "Ledger", description = "가계부 CRUD / 가계부 홈 / 가계부 캘린더")
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "가계부 내역 생성",
            description =
                    """
                    로그인한 사용자 기준으로 수입/지출 내역을 생성합니다.<br>
                    요청 바디의 date를 비우거나 null로 보내면 서버에서 오늘 날짜(LocalDate.now())로 처리하도록
                    서비스/DTO 쪽에서 기본값을 줄 수 있습니다.
                    payment는 CARD / CASH / ETC
                    """)
    public LedgerResponse createLedger(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody LedgerCreateRequest request) {
        return ledgerService.createLedger(userId, request);
    }

    @DeleteMapping("/{ledgerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "가계부 내역 삭제", description = "로그인한 사용자의 본인 가계부 내역만 삭제합니다.")
    public void deleteLedger(
            @Parameter(hidden = true) @CurrentUser Long userId, @PathVariable Long ledgerId) {
        ledgerService.deleteLedger(userId, ledgerId);
    }

    @PatchMapping("/{ledgerId}")
    @Operation(summary = "가계부 내역 수정", description = "로그인한 사용자의 본인 가계부 내역을 수정합니다.")
    public LedgerResponse updateLedger(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @PathVariable Long ledgerId,
            @RequestBody LedgerUpdateRequest request) {
        return ledgerService.updateLedger(userId, ledgerId, request);
    }

    @GetMapping("/calendar")
    @Operation(
            summary = "달력 일간 요약 조회",
            description =
                    """
                    로그인한 사용자의 특정 날짜(또는 오늘)의 가계부 요약을 조회합니다.<br>
                    - 해당 날짜의 수입/지출 합계<br>
                    - 지출 기준 카테고리별 비율<br>
                    - 해당 날짜의 전체 내역 리스트<br>
                    date 파라미터를 생략하면 오늘(LocalDate.now()) 기준으로 조회합니다.
                    """)
    public LedgerCalendarResponse getCalendarSummary(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(description = "조회 기준 날짜 (yyyy-MM-dd). 비우면 오늘 날짜.", required = false)
                    @RequestParam(required = false)
                    LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ledgerService.getCalendarSummary(userId, targetDate);
    }

    @GetMapping("/home")
    @Operation(
            summary = "가계부 홈 화면 요약",
            description =
                    """
                    홈 화면에 필요한 요약 데이터를 조회합니다.<br>
                    - 오늘(또는 선택한 날짜)의 총 지출 + 지출 내역 리스트<br>
                    - 오늘 카테고리별 지출 합계 및 비율<br>
                    - 해당 월 전체 지출 합계 + 카테고리별 비율(도넛 차트용)<br>
                    date 파라미터를 생략하면 오늘(LocalDate.now()) 기준으로 조회합니다.
                    """)
    public LedgerHomeResponse getLedgerHome(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @Parameter(description = "조회 기준 날짜 (yyyy-MM-dd). 비우면 오늘 날짜.", required = false)
                    @RequestParam(required = false)
                    LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ledgerService.getLedgerHome(userId, targetDate);
    }
}
