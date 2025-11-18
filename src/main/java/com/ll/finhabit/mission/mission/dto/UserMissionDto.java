package com.ll.finhabit.mission.mission.dto;

import com.ll.finhabit.mission.mission.entity.UserMission;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMissionDto {

    private Long userMissionId;
    private Long missionId;
    private String missionContent;
    private Integer missionPoint;

    private Integer status;       // enum 대신 Integer
    private boolean completed;
    private Integer progress;

    public static UserMissionDto from(UserMission um) {
        return UserMissionDto.builder()
                .userMissionId(um.getId())
                .missionId(um.getMission().getId())
                .missionContent(um.getMission().getContent())
                .missionPoint(um.getMission().getPoint())
                .status(um.getStatus())
                .completed(um.isCompleted())
                .progress(um.getProgress())
                .build();
    }
}