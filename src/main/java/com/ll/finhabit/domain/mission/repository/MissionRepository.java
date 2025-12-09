package com.ll.finhabit.domain.mission.repository;

import com.ll.finhabit.domain.mission.entity.Mission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    // 유저 레벨 이하 미션만 후보로
    List<Mission> findByMissionLevelLessThanEqual(Integer missionLevel);
}
