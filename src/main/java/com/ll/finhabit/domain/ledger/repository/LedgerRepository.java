package com.ll.finhabit.domain.ledger.repository;

import com.ll.finhabit.domain.ledger.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    List<Ledger> findAllByUser_Id(Long userId);
}
