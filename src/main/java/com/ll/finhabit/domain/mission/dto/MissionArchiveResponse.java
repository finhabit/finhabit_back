package com.ll.finhabit.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MissionArchiveResponse {

    private LocalDate weekStart;      // 주 시작일(월요일 기준 등)
    private LocalDate weekEnd;        // 주 종료일
    private List<MissionProgressDto> missions;
}
