package com.ll.finhabit.domain.finance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer quizId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(length = 50, nullable = false)
    private String option1;

    @Column(length = 50, nullable = false)
    private String option2;

    @Column(length = 50, nullable = false)
    private String option3;

    @Column(nullable = false)
    private Integer answer;

    @Column(columnDefinition = "TEXT")
    private String explanation;
}
