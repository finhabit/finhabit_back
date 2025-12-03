package com.ll.finhabit.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MissionArchiveResponse {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<MissionProgressDto> missions; // 완료된 미션들
}
