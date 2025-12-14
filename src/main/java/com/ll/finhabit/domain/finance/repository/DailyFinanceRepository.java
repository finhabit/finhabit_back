package com.ll.finhabit.domain.finance.repository;

import com.ll.finhabit.domain.finance.entity.DailyFinance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyFinanceRepository extends JpaRepository<DailyFinance, Long> {
    List<DailyFinance> findByCardLevelOrderByCreatedDateAsc(Integer cardLevel);

    Optional<DailyFinance> findByFinanceId(Integer financeId);

    Optional<DailyFinance> findTopByCreatedDateOrderByIdDesc(LocalDate createdDate);

    Optional<DailyFinance> findTopByOrderByCreatedDateDescIdDesc();
}
