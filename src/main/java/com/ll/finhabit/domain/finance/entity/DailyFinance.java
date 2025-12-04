package com.ll.finhabit.domain.finance.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DailyFinance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyFinance {

    @Id
    private Integer quizId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "quizId")
    private Quiz quiz;

    private String cardContent;
    private String cardTitle;
    private LocalDate date;
    private String cardLevel;
}