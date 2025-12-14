package com.ll.finhabit.domain.finance.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ll.finhabit.domain.finance.entity.DailyFinance;

public interface DailyFinanceRepository extends JpaRepository<DailyFinance, Long> {
    // ✨ findByCardLevel 메서드의 파라미터가 Integer로 변경됨
    List<DailyFinance> findByCardLevelOrderByCreatedDateAsc(Integer cardLevel);
    Optional<DailyFinance> findByFinanceId(Integer financeId);
}