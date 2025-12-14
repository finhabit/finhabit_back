package com.ll.finhabit.domain.finance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ll.finhabit.domain.finance.entity.UserKnowledge;

public interface UserKnowledgeRepository extends JpaRepository<UserKnowledge, Long> {
    Optional<UserKnowledge> findByUserIdAndFinanceId(Long userId, Integer financeId);
    List<UserKnowledge> findByUserId(Long userId);
    List<UserKnowledge> findByUserIdAndOpendDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    Optional<UserKnowledge> findByUserIdAndOpendDate(Long userId, LocalDate opendDate);
}