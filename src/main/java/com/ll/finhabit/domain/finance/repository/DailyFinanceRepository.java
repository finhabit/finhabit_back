package com.ll.finhabit.domain.finance.repository;

import com.ll.finhabit.domain.finance.entity.DailyFinance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyFinanceRepository extends JpaRepository<DailyFinance, Long> {
    List<DailyFinance> findByCardLevelOrderByCreatedDateAsc(Integer cardLevel);

    Optional<DailyFinance> findByFinanceId(Integer financeId);

    Optional<DailyFinance> findTopByCreatedDateOrderByIdDesc(LocalDate createdDate);

    Optional<DailyFinance> findTopByOrderByCreatedDateDescIdDesc();
}
