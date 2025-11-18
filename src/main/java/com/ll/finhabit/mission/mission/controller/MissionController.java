package com.ll.finhabit.mission.mission.controller;

import com.ll.finhabit.mission.mission.dto.MissionDto;
import com.ll.finhabit.mission.mission.dto.MissionStatusDto;
import com.ll.finhabit.mission.mission.dto.UserMissionDto;
import com.ll.finhabit.mission.mission.service.MissionService;
import com.ll.finhabit.mission.mission.service.UserMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;
    private final UserMissionService userMissionService;

    // 전체 미션 목록
    @GetMapping
    public List<MissionDto> getMissions() {
        return missionService.getMissionList();
    }

    // 미션 시작
    @PostMapping("/{missionId}/start")
    public UserMissionDto startMission(
            @PathVariable Long missionId,
            @RequestParam Long userId
    ) {
        return userMissionService.startMission(userId, missionId);
    }

    // 진도 업데이트
    @PatchMapping("/user-missions/{userMissionId}/progress")
    public UserMissionDto updateProgress(
            @PathVariable Long userMissionId,
            @RequestParam int progress
    ) {
        return userMissionService.updateProgress(userMissionId, progress);
    }

    // 사용자 미션 전체
    @GetMapping("/user/{userId}")
    public List<UserMissionDto> getUserMissions(@PathVariable Long userId) {
        return userMissionService.getUserMissions(userId);
    }

    // 완료된 미션
    @GetMapping("/user/{userId}/completed")
    public List<UserMissionDto> getCompletedMissions(@PathVariable Long userId) {
        return userMissionService.getCompletedMissions(userId);
    }

    // 통계
    @GetMapping("/user/{userId}/status")
    public MissionStatusDto getStatus(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "WEEK") String period
    ) {
        return userMissionService.getStatus(userId, period);
    }
}