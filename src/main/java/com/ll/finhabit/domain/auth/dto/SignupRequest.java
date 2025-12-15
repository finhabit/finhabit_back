package com.ll.finhabit.domain.auth.dto;

import com.ll.finhabit.global.common.ValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SignupRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(
            max = ValidationRules.NICKNAME_MAX_LENGTH,
            message = "닉네임은 15자 이하로 입력해주세요."
    )
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = ValidationRules.PASSWORD_REGEX,
            message = "비밀번호는 8~16자의 영문, 숫자, 특수문자 조합이어야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    private List<LevelTestAnswer> levelTestAnswers;
}
