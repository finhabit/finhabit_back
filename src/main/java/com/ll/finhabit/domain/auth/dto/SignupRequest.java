package com.ll.finhabit.domain.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class SignupRequest {
    private String nickname;
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;

    private List<LevelTestAnswer> levelTestAnswers;
}
