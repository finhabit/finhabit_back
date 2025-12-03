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

    // 오늘의 미션 + 진행률
    @GetMapping
    public MissionTodayResponse getMissionOverview(@RequestParam("userId") Long userId) {
        return missionService.getMissionToday(userId);
    }

    // 미션 체크
    @PostMapping("/{userMissionId}/check")
    public MissionProgressDto checkMission(@PathVariable("userMissionId") Long userMissionId,
                                           @RequestParam("userId") Long userId) {
        return missionService.checkMission(userId, userMissionId);
    }

    // 완료 미션 아카이브
    @GetMapping("/archive")
    public List<MissionArchiveResponse> getMissionArchive(@RequestParam("userId") Long userId) {
        return missionService.getMissionArchive(userId);
    }
}
