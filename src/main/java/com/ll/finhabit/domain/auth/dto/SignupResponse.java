package com.ll.finhabit.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private Long id;
    private String nickname;
    private String email;

    private Integer level;
    private Integer correctCount;     // 맞춘 개수
    private Integer correctRate;      // 맞춘 비율
}

