package com.ll.finhabit.domain.ledger.repository;

import com.ll.finhabit.domain.ledger.entity.Ledger;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    List<Ledger> findAllByUser_Id(Long userId);

    List<Ledger> findAllByUser_IdAndDate(Long userId, LocalDate date);

    List<Ledger> findAllByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}
