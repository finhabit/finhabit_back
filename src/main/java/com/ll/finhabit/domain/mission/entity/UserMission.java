package com.ll.finhabit.domain.mission.entity;

import com.ll.finhabit.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usermission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usermission_id")
    private Long usermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "progress", nullable = false)
    private Integer progress;

    @Column(name = "done_count", nullable = false)
    private Integer doneCount;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Column(name = "week_start")
    private LocalDate weekStart;
}
