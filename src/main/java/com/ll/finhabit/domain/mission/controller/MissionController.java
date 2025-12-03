package com.ll.finhabit.domain.mission.controller;

import com.ll.finhabit.domain.mission.dto.MissionArchiveResponse;
import com.ll.finhabit.domain.mission.dto.MissionProgressDto;
import com.ll.finhabit.domain.mission.dto.MissionTodayResponse;
import com.ll.finhabit.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/{userId}")
    public MissionTodayResponse getTodayMission(@PathVariable Long userId) {
        return missionService.getMissionToday(userId);
    }

    @PostMapping("/{userId}/{userMissionId}/check")
    public MissionProgressDto checkMission(
            @PathVariable Long userId,
            @PathVariable Long userMissionId
    ) {
        return missionService.checkMission(userId, userMissionId);
    }

    @GetMapping("/{userId}/archive")
    public List<MissionArchiveResponse> getMissionArchive(
            @PathVariable Long userId
    ) {
        return missionService.getMissionArchive(userId);
    }
}
