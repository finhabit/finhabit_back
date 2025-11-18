package com.ll.finhabit.mission.mission.repository;

import com.ll.finhabit.mission.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    List<UserMission> findByUserId(Long userId);

    List<UserMission> findByUserIdAndCompletedTrue(Long userId);

    List<UserMission> findByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
}