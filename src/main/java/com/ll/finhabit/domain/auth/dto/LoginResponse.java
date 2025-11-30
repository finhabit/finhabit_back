package com.ll.finhabit.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long id;
    private String nickname;
    private String email;
    private Integer level;
    private Integer userPoint;
}
