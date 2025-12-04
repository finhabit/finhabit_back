package com.ll.finhabit.domain.finance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.finhabit.domain.finance.entity.DailyFinance;

@Repository
public interface DailyFinanceRepository extends JpaRepository<DailyFinance, Integer> {

    DailyFinance findByDate(LocalDate date);
    List<DailyFinance> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}