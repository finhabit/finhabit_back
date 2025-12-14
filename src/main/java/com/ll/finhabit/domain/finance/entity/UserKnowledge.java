package com.ll.finhabit.domain.finance.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "UserKnowledge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserKnowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer financeId;
    private Long userId;

    private LocalDate opendDate;
    private LocalDate viewedAt;
}