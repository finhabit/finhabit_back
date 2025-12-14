package com.ll.finhabit.domain.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DailyFinance")
@Getter
@Setter
public class DailyFinance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer financeId;

    @Column(columnDefinition = "TEXT")
    private String cardContent;

    @Column(length = 30)
    private String cardTitle;

    private Integer cardLevel;

    private LocalDate createdDate;
}
