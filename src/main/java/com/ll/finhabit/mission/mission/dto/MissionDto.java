package com.ll.finhabit.mission.mission.dto;

import com.ll.finhabit.mission.mission.entity.Mission;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MissionDto {
    private Long id;
    private String content;
    private Integer point;

    public static MissionDto from(Mission mission) {
        return MissionDto.builder()
                .id(mission.getId())
                .content(mission.getContent())
                .point(mission.getPoint())
                .build();
    }
}