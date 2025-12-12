package com.ll.finhabit.domain.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false, length = 15)
    private String categoryName;

    @Column(nullable = false, length = 10)
    private String type; // 지출 혹은 수입

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Ledger> ledgers = new ArrayList<>();
}
