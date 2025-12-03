package com.ll.finhabit.domain.mission.entity;

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
    private Long missionId;

    @Column(name = "mission_content", length = 50, nullable = false)
    private String missionContent;

    @Column(name = "mission_level", nullable = false)
    private Integer missionLevel;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;    // 총 수행 횟수
}
