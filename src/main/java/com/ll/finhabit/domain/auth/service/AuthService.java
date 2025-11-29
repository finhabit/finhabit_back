package com.ll.finhabit.domain.auth.service;

import com.ll.finhabit.domain.auth.dto.LevelTestAnswer;
import com.ll.finhabit.domain.auth.dto.SignupRequest;
import com.ll.finhabit.domain.auth.dto.SignupResponse;
import com.ll.finhabit.domain.auth.entity.LevelTest;
import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.entity.UserLevel;
import com.ll.finhabit.domain.auth.repository.LevelTestRepository;
import com.ll.finhabit.domain.auth.repository.UserLevelRepository;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LevelTestRepository levelTestRepository;
    private final UserLevelRepository userLevelRepository;

    @Transactional
    public SignupResponse signup(SignupRequest req) {

        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        User user = User.builder()
                .nickname(req.getNickname())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        User saved = userRepository.save(user);

        // 레벨 테스트 채점 + UserLevel 저장
        int correctCount = 0;

        if (req.getLevelTestAnswers() != null) {
            for (LevelTestAnswer answerDto : req.getLevelTestAnswers()) {
                LevelTest test = levelTestRepository.findById(answerDto.getTestId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문제입니다."));

                boolean isCorrect = test.getTestAnswer().equals(answerDto.getUserAnswer());
                if (isCorrect) {
                    correctCount++;
                }

                UserLevel userLevel = UserLevel.builder()
                        .userId(saved.getId())
                        .test(test)
                        .isCorrect(isCorrect)
                        .userAnswer(answerDto.getUserAnswer())
                        .build();

                userLevelRepository.save(userLevel);
            }
        }

        int level = 1;
        if (correctCount >= 4) level = 3;
        else if (correctCount >= 2) level = 2;

        saved.setLevel(level);

        return SignupResponse.builder()
                .id(saved.getId())
                .nickname(saved.getNickname())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .build();
    }

}