package com.ll.finhabit.mission.mission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    // 미션 내용 (예: 오늘 커피값 아끼기)
    @Column(name = "mission_content", length = 50, nullable = false)
    private String content;

    // 미션 완료 시 지급할 포인트
    @Column(name = "mission_point", nullable = false)
    private Integer point;
}