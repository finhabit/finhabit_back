package com.ll.finhabit.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private Long id;
    private String nickname;
    private String username;
    private String email;
}

