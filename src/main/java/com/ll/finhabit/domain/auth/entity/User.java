package com.ll.finhabit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @Column(nullable = false, length = 15)
    private String nickname;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "user_point", nullable = false)
    @Builder.Default
    private Integer userPoint = 0; // 디폴트 0

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1; // 디폴트 1
}
