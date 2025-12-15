package com.ll.finhabit.domain.auth.dto;

import com.ll.finhabit.global.common.ValidationRules;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateDto {
    private String currentPassword;

    @Pattern(
            regexp = ValidationRules.PASSWORD_REGEX,
            message = "비밀번호는 8~16자의 영문, 숫자, 특수문자 조합이어야 합니다."
    )
    private String newPassword;

    private String newPasswordConfirm;
}
