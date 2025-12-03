package com.ll.finhabit.domain.mission.repository;

import com.ll.finhabit.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    // 유저 레벨 이하 미션만 후보로
    List<Mission> findByMissionLevelLessThanEqual(Integer missionLevel);
}
