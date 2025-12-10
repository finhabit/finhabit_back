package com.ll.finhabit.domain.mission.repository;

import com.ll.finhabit.domain.mission.entity.UserMission;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    // 오늘 이미 배정된 미션 있는지
    Optional<UserMission> findByUser_IdAndAssignedDate(Long userId, LocalDate assignedDate);

    // 이번 주에 특정 미션 템플릿이 몇 번이나 배정되었는지
    long countByUser_IdAndMission_MissionIdAndWeekStart(
            Long userId, Long missionId, LocalDate weekStart);

    // 이번 주 + 미완료 미션 조회
    List<UserMission> findByUser_IdAndWeekStartAndIsCompletedFalseOrderByUsermissionIdAsc(
            Long userId, LocalDate weekStart);

    // 이번 주 + 미완료 미션 중 첫 번째(오늘의 미션)
    Optional<UserMission> findFirstByUser_IdAndWeekStartAndIsCompletedFalseOrderByUsermissionIdAsc(
            Long userId, LocalDate weekStart);

    // 이번 주 + 완료된 미션 조회(아카이브)
    List<UserMission> findByUser_IdAndWeekStartAndIsCompletedTrueOrderByCompletedAtDesc(
            Long userId, LocalDate weekStart);

    // 이번 주에 이 유저의 모든 미션
    List<UserMission> findByUser_IdAndWeekStart(Long userId, LocalDate weekStart);

    // 아카이브 전체 조회용: weekStart 있는 완료된 미션들
    List<UserMission> findByUser_IdAndIsCompletedTrueAndWeekStartIsNotNull(Long userId);
}
