package com.ll.finhabit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leveltest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LevelTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testId")
    private Long testId;

    @Column(name = "test_category", length = 10, nullable = false)
    private String testCategory;

    @Column(name = "test_question", length = 30, nullable = false)
    private String testQuestion;

    @Column(name = "test_option1", length = 20, nullable = false)
    private String testOption1;

    @Column(name = "test_option2", length = 30, nullable = false)
    private String testOption2;

    @Column(name = "test_option3", length = 30, nullable = false)
    private String testOption3;

    @Column(name = "test_answer", nullable = false)
    private Integer testAnswer;
}
