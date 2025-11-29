package com.ll.finhabit.domain.auth.dto;

import lombok.Data;

@Data
public class LevelTestAnswer {
    private Long testId;        // 어떤 문제를 풀었는지
    private Integer userAnswer; // 유저가 고른 보기
}
