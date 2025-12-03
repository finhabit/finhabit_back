package com.ll.finhabit.domain.mission.repository;

import com.ll.finhabit.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
