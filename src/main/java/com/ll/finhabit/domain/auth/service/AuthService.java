package com.ll.finhabit.domain.auth.service;

import com.ll.finhabit.domain.auth.dto.*;
import com.ll.finhabit.domain.auth.entity.User;
import com.ll.finhabit.domain.auth.repository.UserLevelRepository;
import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.mission.repository.UserMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserMissionRepository userMissionRepository;
    private final UserLevelRepository userLevelRepository;

    // 분리한 컴포넌트
    private final AuthValidator authValidator;
    private final LevelTestGrader levelTestGrader;

    @Transactional
    public SignupResponse signup(SignupRequest req) {

        authValidator.validateSignup(req);

        User user = User.builder()
                .nickname(req.getNickname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        User saved = userRepository.save(user);

        var result = levelTestGrader.gradeAndSave(saved, req.getLevelTestAnswers());

        saved.setLevel(result.level());

        return SignupResponse.builder()
                .id(saved.getId())
                .nickname(saved.getNickname())
                .email(saved.getEmail())
                .level(saved.getLevel())
                .correctCount(result.correctCount())
                .correctRate(result.correctRate())
                .build();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }

        return LoginResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .level(user.getLevel())
                .build();
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        userLevelRepository.deleteByUser_Id(userId);
        userMissionRepository.deleteByUser_Id(userId);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return UserProfileResponseDto.from(user);
    }

    @Transactional
    public void updateProfile(Long userId, UserMeUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            user.setNickname(dto.getNickname());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
    }

    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateDto dto) {
        log.info("비밀번호 변경 시도: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 검증 로직을 AuthValidator로 이동할 수도 있음
        if (!passwordEncoder.matches(dto. getCurrentPassword(), user.getPassword())) {
            log.warn("비밀번호 변경 실패 - 현재 비밀번호 불일치: userId={}", userId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다.");
        }

        if (!dto.getNewPassword().equals(dto.getNewPasswordConfirm())) {
            log.warn("비밀번호 변경 실패 - 새 비밀번호 불일치: userId={}", userId);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder. encode(dto.getNewPassword()));
        log.info("비밀번호 변경 완료: userId={}", userId);
    }
}
