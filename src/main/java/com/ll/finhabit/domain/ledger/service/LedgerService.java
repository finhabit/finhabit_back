package com.ll.finhabit.domain.ledger.service;

import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.ledger.dto.LedgerCreateRequest;
import com.ll.finhabit.domain.ledger.dto.LedgerResponse;
import com.ll.finhabit.domain.ledger.dto.LedgerUpdateRequest;
import com.ll.finhabit.domain.ledger.entity.Category;
import com.ll.finhabit.domain.ledger.entity.Ledger;
import com.ll.finhabit.domain.ledger.entity.PaymentType;
import com.ll.finhabit.domain.ledger.repository.CategoryRepository;
import com.ll.finhabit.domain.ledger.repository.LedgerRepository;
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

    // 소비내역 생성
    public LedgerResponse createLedger(Long userId, LedgerCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."));

        Ledger ledger = Ledger.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .memo(request.getMemo())
                .merchant(request.getMerchant())
                .date(request.getDate())
                .payment(PaymentType.valueOf(request.getPayment()))
                .build();

        Ledger saved = ledgerRepository.save(ledger);
        return LedgerResponse.from(saved);
    }


    //소비내역 수정
    public LedgerResponse updateLedger(Long userId, Long ledgerId, LedgerUpdateRequest request) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 가계부 내역입니다."));

        // 소유자 검증
        if (!ledger.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 소비 내역만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."));

        ledger.setCategory(category);
        ledger.setAmount(request.getAmount());
        ledger.setMemo(request.getMemo());
        ledger.setMerchant(request.getMerchant());
        ledger.setDate(request.getDate());
        ledger.setPayment(PaymentType.valueOf(request.getPayment()));

        return LedgerResponse.from(ledger);
    }

    //소비내역 삭제
    public void deleteLedger(Long userId, Long ledgerId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 가계부 내역입니다."));

        if (!ledger.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 소비 내역만 삭제할 수 있습니다.");
        }

        ledgerRepository.delete(ledger);
    }
}
