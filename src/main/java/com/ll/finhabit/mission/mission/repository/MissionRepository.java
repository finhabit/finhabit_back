package com.ll.finhabit.mission.mission.repository;

import com.ll.finhabit.mission.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}