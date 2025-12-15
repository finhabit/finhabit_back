package com.ll.finhabit.domain.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer quizId;

    @Column(nullable = false)
    private LocalDate attemptedDate;

    @Column(nullable = false)
    private Boolean isAnswered = false;

    @Column(nullable = false)
    private Boolean isCorrect = false;

    private Integer selectedAnswer;
}
