package com.ll.finhabit.domain.ledger.service;

import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.ledger.dto.*;
import com.ll.finhabit.domain.ledger.entity.Category;
import com.ll.finhabit.domain.ledger.entity.Ledger;
import com.ll.finhabit.domain.ledger.entity.PaymentType;
import com.ll.finhabit.domain.ledger.repository.CategoryRepository;
import com.ll.finhabit.domain.ledger.repository.LedgerRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // --------------------
    // CREATE
    // --------------------
    public LedgerResponse createLedger(Long userId, LedgerCreateRequest request) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."));

        boolean isIncome = "income".equals(category.getType());

        if (!isIncome && request.getPayment() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지출은 결제 수단이 필요합니다.");
        }

        PaymentType payment = isIncome ? null : request.getPayment();

        // 날짜가 없으면 자동으로 오늘 날짜 적용
        LocalDate useDate = (request.getDate() != null) ? request.getDate() : LocalDate.now();

        Ledger ledger =
                Ledger.builder()
                        .user(user)
                        .category(category)
                        .amount(request.getAmount())
                        .merchant(request.getMerchant())
                        .date(useDate)
                        .payment(payment)
                        .build();

        Ledger saved = ledgerRepository.save(ledger);
        return toLedgerResponse(saved);
    }

    // --------------------
    // UPDATE
    // --------------------
    public LedgerResponse updateLedger(Long userId, Long ledgerId, LedgerUpdateRequest request) {

        Ledger ledger =
                ledgerRepository
                        .findById(ledgerId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "존재하지 않는 가계부 내역입니다."));

        if (!ledger.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 소비 내역만 수정할 수 있습니다.");
        }

        Category category =
                categoryRepository
                        .findById(request.getCategoryId())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."));

        boolean isIncome = "income".equals(category.getType());

        if (!isIncome && request.getPayment() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지출은 결제 수단이 필요합니다.");
        }

        PaymentType payment = isIncome ? null : request.getPayment();

        ledger.setCategory(category);
        ledger.setAmount(request.getAmount());
        ledger.setMerchant(request.getMerchant());
        ledger.setDate(request.getDate());
        ledger.setPayment(payment);

        return toLedgerResponse(ledger);
    }

    // --------------------
    // DELETE
    // --------------------
    public void deleteLedger(Long userId, Long ledgerId) {

        Ledger ledger =
                ledgerRepository
                        .findById(ledgerId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "존재하지 않는 가계부 내역입니다."));

        if (!ledger.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 소비 내역만 삭제할 수 있습니다.");
        }

        ledgerRepository.delete(ledger);
    }

    // --------------------
    // DAILY / CALENDAR SUMMARY
    // --------------------
    public LedgerCalendarResponse getCalendarSummary(Long userId, LocalDate date) {

        List<Ledger> ledgers = ledgerRepository.findAllByUser_IdAndDate(userId, date);

        int totalIncome =
                ledgers.stream()
                        .filter(l -> "income".equals(l.getCategory().getType()))
                        .mapToInt(Ledger::getAmount)
                        .sum();

        List<Ledger> expenseLedgers =
                ledgers.stream().filter(l -> "expense".equals(l.getCategory().getType())).toList();

        int totalExpense = expenseLedgers.stream().mapToInt(Ledger::getAmount).sum();

        // 카테고리별 지출 합계
        Map<Long, Integer> categoryMap =
                expenseLedgers.stream()
                        .collect(
                                Collectors.groupingBy(
                                        l -> l.getCategory().getCategoryId(),
                                        Collectors.summingInt(Ledger::getAmount)));

        // 카테고리별 비율 계산 (정수 퍼센트)
        List<LedgerHomeResponse.CategoryRatio> categories =
                categoryMap.entrySet().stream()
                        .map(
                                entry -> {
                                    Long categoryId = entry.getKey();
                                    int amount = entry.getValue();

                                    Category category =
                                            expenseLedgers.stream()
                                                    .filter(
                                                            l ->
                                                                    l.getCategory()
                                                                            .getCategoryId()
                                                                            .equals(categoryId))
                                                    .findFirst()
                                                    .orElseThrow()
                                                    .getCategory();

                                    int percent = 0;
                                    if (totalExpense != 0) {
                                        percent = (int) Math.round(amount * 100.0 / totalExpense);
                                    }

                                    return LedgerHomeResponse.CategoryRatio.builder()
                                            .categoryId(categoryId)
                                            .categoryName(category.getCategoryName())
                                            .amount(amount)
                                            .percent(percent)
                                            .build();
                                })
                        .toList();

        List<LedgerResponse> ledgerResponses =
                ledgers.stream().map(this::toLedgerResponse).toList();

        return LedgerCalendarResponse.builder()
                .date(date)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .categories(categories)
                .ledgers(ledgerResponses)
                .build();
    }

    // --------------------
    // HOME SUMMARY
    // --------------------
    public LedgerHomeResponse getLedgerHome(Long userId, LocalDate date) {

        List<Ledger> todayLedgers = ledgerRepository.findAllByUser_IdAndDate(userId, date);

        List<Ledger> todayExpenseLedgers =
                todayLedgers.stream()
                        .filter(l -> "expense".equals(l.getCategory().getType()))
                        .toList();

        int todayTotalExpense = todayExpenseLedgers.stream().mapToInt(Ledger::getAmount).sum();

        List<LedgerResponse> todayLedgerResponses =
                todayExpenseLedgers.stream().map(this::toLedgerResponse).toList();

        LedgerHomeResponse.TodaySpending today =
                LedgerHomeResponse.TodaySpending.builder()
                        .totalExpense(todayTotalExpense)
                        .ledgers(todayLedgerResponses)
                        .build();

        // -----------------------
        // 카테고리별 오늘 소비
        // -----------------------
        List<LedgerHomeResponse.TodayCategory> todayCategories = List.of();

        if (!todayExpenseLedgers.isEmpty()) {

            Map<Long, Integer> todayCategoryMap =
                    todayExpenseLedgers.stream()
                            .collect(
                                    Collectors.groupingBy(
                                            l -> l.getCategory().getCategoryId(),
                                            Collectors.summingInt(Ledger::getAmount)));

            todayCategories =
                    todayCategoryMap.entrySet().stream()
                            .map(
                                    entry -> {
                                        Long categoryId = entry.getKey();
                                        int amount = entry.getValue();

                                        Category category =
                                                todayExpenseLedgers.stream()
                                                        .filter(
                                                                l ->
                                                                        l.getCategory()
                                                                                .getCategoryId()
                                                                                .equals(categoryId))
                                                        .findFirst()
                                                        .orElseThrow()
                                                        .getCategory();

                                        int percent = 0;
                                        if (todayTotalExpense != 0) {
                                            percent =
                                                    (int)
                                                            Math.round(
                                                                    amount
                                                                            * 100.0
                                                                            / todayTotalExpense);
                                        }

                                        return LedgerHomeResponse.TodayCategory.builder()
                                                .categoryId(categoryId)
                                                .categoryName(category.getCategoryName())
                                                .amount(amount)
                                                .percent(percent)
                                                .build();
                                    })
                            .toList();
        }

        // -----------------------
        // 월간 소비 요약
        // -----------------------
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        List<Ledger> monthLedgers =
                ledgerRepository.findAllByUser_IdAndDateBetween(userId, startOfMonth, endOfMonth);

        List<Ledger> monthExpenseLedgers =
                monthLedgers.stream()
                        .filter(l -> "expense".equals(l.getCategory().getType()))
                        .toList();

        int monthTotalExpense = monthExpenseLedgers.stream().mapToInt(Ledger::getAmount).sum();

        Map<Long, Integer> monthCategoryMap =
                monthExpenseLedgers.stream()
                        .collect(
                                Collectors.groupingBy(
                                        l -> l.getCategory().getCategoryId(),
                                        Collectors.summingInt(Ledger::getAmount)));

        List<LedgerHomeResponse.CategoryRatio> monthRatios =
                monthCategoryMap.entrySet().stream()
                        .map(
                                entry -> {
                                    Long categoryId = entry.getKey();
                                    int amount = entry.getValue();

                                    Category category =
                                            monthExpenseLedgers.stream()
                                                    .filter(
                                                            l ->
                                                                    l.getCategory()
                                                                            .getCategoryId()
                                                                            .equals(categoryId))
                                                    .findFirst()
                                                    .orElseThrow()
                                                    .getCategory();

                                    int percent = 0;
                                    if (monthTotalExpense != 0) {
                                        percent =
                                                (int)
                                                        Math.round(
                                                                amount * 100.0 / monthTotalExpense);
                                    }

                                    return LedgerHomeResponse.CategoryRatio.builder()
                                            .categoryId(categoryId)
                                            .categoryName(category.getCategoryName())
                                            .amount(amount)
                                            .percent(percent)
                                            .build();
                                })
                        .toList();

        LedgerHomeResponse.MonthSummary monthSummary =
                LedgerHomeResponse.MonthSummary.builder()
                        .year(date.getYear())
                        .month(date.getMonthValue())
                        .totalExpense(monthTotalExpense)
                        .categories(monthRatios)
                        .build();

        return LedgerHomeResponse.builder()
                .date(date)
                .today(today)
                .todayCategories(todayCategories)
                .monthSummary(monthSummary)
                .build();
    }

    // --------------------
    // DTO 변환 메소드
    // --------------------
    private LedgerResponse toLedgerResponse(Ledger ledger) {
        return LedgerResponse.builder()
                .ledgerId(ledger.getLedgerId())
                .categoryId(ledger.getCategory().getCategoryId())
                .categoryName(ledger.getCategory().getCategoryName())
                .type(ledger.getCategory().getType())
                .amount(ledger.getAmount())
                .merchant(ledger.getMerchant())
                .date(ledger.getDate())
                .payment(ledger.getPayment() != null ? ledger.getPayment().name() : null) // NPE 방지
                .build();
    }
}
