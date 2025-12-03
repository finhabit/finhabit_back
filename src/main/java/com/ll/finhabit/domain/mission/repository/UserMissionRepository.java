package com.ll.finhabit.domain.mission.repository;

import com.ll.finhabit.domain.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    // 이번 주 + 미완료 미션 조회
    List<UserMission> findByUser_UserIdAndWeekStartAndIsCompletedFalseOrderByUsermissionIdAsc(
            Long userId,
            LocalDate weekStart
    );

    // 이번 주 + 미완료 미션 중 첫 번째
    Optional<UserMission> findFirstByUser_UserIdAndWeekStartAndIsCompletedFalseOrderByUsermissionIdAsc(
            Long userId,
            LocalDate weekStart
    );

    // 이번 주 + 완료된 미션 조회
    List<UserMission> findByUser_UserIdAndWeekStartAndIsCompletedTrueOrderByCompletedAtDesc(
            Long userId,
            LocalDate weekStart
    );
}
