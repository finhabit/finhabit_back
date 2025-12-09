package com.ll.finhabit.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionProgressDto {

    private Long userMissionId;

    private Long missionId;
    private String missionContent;
    private Integer missionPoint;
    private Integer missionLevel;

    private Integer totalCount; // 목표 횟수
    private Integer doneCount; // 현재까지 수행 횟수
    private Integer progress; // 퍼센트(0~100)

    private Boolean completed;
}
