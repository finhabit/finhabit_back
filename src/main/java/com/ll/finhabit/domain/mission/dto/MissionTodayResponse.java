package com.ll.finhabit.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionTodayResponse {
    private MissionProgressDto todayMission;
}
