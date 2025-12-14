package com.ll.finhabit.domain.finance.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ll.finhabit.domain.finance.entity.DailyFinance;

public interface DailyFinanceRepository extends JpaRepository<DailyFinance, Long> {
    List<DailyFinance> findByCardLevelOrderByCreatedDateAsc(Integer cardLevel);
    Optional<DailyFinance> findByFinanceId(Integer financeId);
}