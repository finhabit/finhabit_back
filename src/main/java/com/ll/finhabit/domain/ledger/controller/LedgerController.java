package com.ll.finhabit.domain.ledger.controller;

import com.ll.finhabit.domain.ledger.dto.LedgerCreateRequest;
import com.ll.finhabit.domain.ledger.dto.LedgerResponse;
import com.ll.finhabit.domain.ledger.dto.LedgerUpdateRequest;
import com.ll.finhabit.domain.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LedgerResponse createLedger(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody LedgerCreateRequest request
    ) {
        return ledgerService.createLedger(userId, request);
    }

    @DeleteMapping("/{ledgerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLedger(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long ledgerId
    ) {
        ledgerService.deleteLedger(userId, ledgerId);
    }

    @PatchMapping("/{ledgerId}")
    public LedgerResponse updateLedger(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long ledgerId,
            @RequestBody LedgerUpdateRequest request
    ) {
        return ledgerService.updateLedger(userId, ledgerId, request);
    }


}
