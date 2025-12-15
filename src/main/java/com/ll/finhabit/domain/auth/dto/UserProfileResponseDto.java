package com.ll.finhabit.domain.auth.dto;

import com.ll.finhabit.domain.auth.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {
    private final String nickname;
    private final String email;
    private final Integer level;
    private final String maskedPassword;

    public static UserProfileResponseDto from(User user) {
        final String MASKED_STRING = "********";

        return UserProfileResponseDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .level(user.getLevel())
                .maskedPassword(MASKED_STRING)
                .build();
    }
}
