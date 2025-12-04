package com.ll.finhabit.domain.finance.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer quizId;

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private Integer answer;
    private String explanation;

    @OneToOne(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DailyFinance dailyFinance;
}