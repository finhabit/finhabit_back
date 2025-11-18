package com.ll.finhabit.mission.mission.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MissionStatusDto {
    private String periodType;   // 주or달 이런 거...
    private long totalCount;
    private long completedCount;
    private double completionRate;
}