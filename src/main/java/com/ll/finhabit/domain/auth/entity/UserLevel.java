package com.ll.finhabit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "userlevel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userlevel_id")
    private Long userLevelId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private LevelTest test;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "user_answer", nullable = false)
    private Integer userAnswer;
}
