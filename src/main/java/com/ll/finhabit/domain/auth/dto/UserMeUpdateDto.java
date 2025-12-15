package com.ll.finhabit.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMeUpdateDto {
    @Size(max = 15, message = "15자 이하로 입력해주세요.")
    private String nickname;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}