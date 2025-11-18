package com.ll.finhabit.mission.mission.service;

import com.ll.finhabit.mission.mission.dto.MissionDto;
import com.ll.finhabit.mission.mission.entity.Mission;
import com.ll.finhabit.mission.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;

    public List<MissionDto> getMissionList() {
        return missionRepository.findAll().stream()
                .map(MissionDto::from)
                .toList();
    }

    public Mission getMission(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("미션이 존재하지 않습니다."));
    }
}