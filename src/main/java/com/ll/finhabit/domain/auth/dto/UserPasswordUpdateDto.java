package com.ll.finhabit.domain.auth.dto;

import com.ll.finhabit.global.common.ValidationRules;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateDto {
    private String currentPassword;

    @Pattern(regexp = ValidationRules.PASSWORD_REGEX)
    private String newPassword;

    private String newPasswordConfirm;
}
