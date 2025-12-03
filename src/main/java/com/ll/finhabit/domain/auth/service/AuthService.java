package com.ll.finhabit.domain.auth.service;

import com.ll.finhabit.domain.auth.dto.*;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.entity.UserLevel;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.repository.UserLevelRepository;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LevelTestRepository levelTestRepository;
    private final UserLevelRepository userLevelRepository;

    private static final int TOTAL_QUESTIONS = 5;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private void validateEmailFormat(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        }
    }

    @Transactional
    public SignupResponse signup(SignupRequest req) {

        validateEmailFormat(req.getEmail());

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }

        User user =
                User.builder()
                        .nickname(req.getNickname())
                        .email(req.getEmail())
                        .password(passwordEncoder.encode(req.getPassword()))
                        .build();

        User saved = userRepository.save(user);

        int correctCount = 0;

        if (req.getLevelTestAnswers() != null) {
            for (LevelTestAnswer answerDto : req.getLevelTestAnswers()) {
                LevelTest test =
                        levelTestRepository
                                .findById(answerDto.getTestId())
                                .orElseThrow(
                                        () ->
                                                new ResponseStatusException(
                                                        HttpStatus.BAD_REQUEST, "존재하지 않는 문제입니다."));

                boolean isCorrect = test.getTestAnswer().equals(answerDto.getUserAnswer());
                if (isCorrect) {
                    correctCount++;
                }

                UserLevel userLevel =
                        UserLevel.builder()
                                .user(saved)
                                .test(test)
                                .isCorrect(isCorrect)
                                .userAnswer(answerDto.getUserAnswer())
                                .build();

                userLevelRepository.save(userLevel);
            }
        }

        // 맞춘 개수 기반 레벨 계산
        int level = 1;
        if (correctCount >= 4) {
            level = 3;
        } else if (correctCount >= 2) {
            level = 2;
        }

        saved.setLevel(level);

        int correctRate = (int) Math.round(correctCount * 100.0 / TOTAL_QUESTIONS);

        return SignupResponse.builder()
                .id(saved.getUserId())
                .nickname(saved.getNickname())
                .email(saved.getEmail())
                .level(saved.getLevel())
                .correctCount(correctCount) // 맞춘 개수
                .correctRate(correctRate) // 맞춘 비율(%)
                .build();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public LoginResponse login(LoginRequest req) {

        User user =
                userRepository
                        .findByEmail(req.getEmail())
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }

        return LoginResponse.builder()
                .id(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .level(user.getLevel())
                .build();
    }
}
