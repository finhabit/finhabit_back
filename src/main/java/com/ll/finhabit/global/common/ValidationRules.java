package com.ll.finhabit.global.common;

public final class ValidationRules {

    private ValidationRules() {}

    public static final String PASSWORD_REGEX =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$";

    public static final int NICKNAME_MAX_LENGTH = 15;
}
