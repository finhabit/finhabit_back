package com.ll.finhabit.domain.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateDto {
    private String currentPassword;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
            message = "비밀번호는 8~16자의 영문, 숫자, 특수문자 조합이어야 합니다.")
    private String newPassword;

    // 새 비밀번호 확인 (서비스 단에서 newPassword와 일치 확인 필요)
    private String newPasswordConfirm;
}
