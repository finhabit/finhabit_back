package com.ll.finhabit.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MissionTodayResponse {

    // 오늘의 미션(하나)
    private MissionProgressDto todayMission;

    // 진행 중인 모든 미션 (막대그래프용)
    private List<MissionProgressDto> ongoingMissions;
}
