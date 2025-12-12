package com.ll.finhabit.domain.mission.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionArchiveResponse {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private List<MissionProgressDto> missions; // 완료된 미션들
}
