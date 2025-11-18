package com.ll.finhabit.mission.mission.service;

import com.ll.finhabit.mission.mission.dto.MissionStatusDto;
import com.ll.finhabit.mission.mission.dto.UserMissionDto;
import com.ll.finhabit.mission.mission.entity.Mission;
import com.ll.finhabit.mission.mission.entity.UserMission;
import com.ll.finhabit.mission.mission.repository.UserMissionRepository;
import com.ll.finhabit.user.entity.User;
import com.ll.finhabit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMissionService {

    private final UserMissionRepository userMissionRepository;
    private final MissionService missionService;
    private final UserRepository userRepository;

    // 미션 시작
    public UserMissionDto startMission(Long userId, Long missionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Mission mission = missionService.getMission(missionId);

        UserMission userMission = UserMission.builder()
                .user(user)
                .mission(mission)
                .status(1)          // 1 = 진행중
                .completed(false)
                .progress(0)
                .build();

        return UserMissionDto.from(userMissionRepository.save(userMission));
    }

    // 진행률 업데이트
    public UserMissionDto updateProgress(Long userMissionId, int progress) {

        UserMission um = userMissionRepository.findById(userMissionId)
                .orElseThrow(() -> new IllegalArgumentException("UserMission not found"));

        um.setProgress(progress);

        // 100%면 완료 처리
        if (progress >= 100 && !um.isCompleted()) {
            completeMission(um);
        }

        return UserMissionDto.from(um);
    }

    private void completeMission(UserMission um) {
        um.setCompleted(true);
        um.setStatus(2); // 2=완료

        User user = um.getUser();
        int current = user.getUserPoint() == null ? 0 : user.getUserPoint();
        user.setUserPoint(current + um.getMission().getPoint());
    }

    @Transactional(readOnly = true)
    public List<UserMissionDto> getUserMissions(Long userId) {
        return userMissionRepository.findByUserId(userId).stream()
                .map(UserMissionDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserMissionDto> getCompletedMissions(Long userId) {
        return userMissionRepository.findByUserIdAndCompletedTrue(userId).stream()
                .map(UserMissionDto::from)
                .toList();
    }

    // 통계
    @Transactional(readOnly = true)
    public MissionStatusDto getStatus(Long userId, String periodType) {

        LocalDateTime start;
        LocalDateTime end;

        if ("MONTH".equalsIgnoreCase(periodType)) {
            LocalDate now = LocalDate.now();
            start = now.withDayOfMonth(1).atStartOfDay();
            end = start.plusMonths(1);
        } else {
            LocalDate now = LocalDate.now();
            LocalDate monday = now.with(DayOfWeek.MONDAY);
            start = monday.atStartOfDay();
            end = start.plusDays(7);
            periodType = "WEEK";
        }

        List<UserMission> list =
                userMissionRepository.findByUserIdAndCreatedAtBetween(userId, start, end);

        long total = list.size();
        long completed = list.stream().filter(UserMission::isCompleted).count();
        double rate = (total == 0 ? 0 : (completed * 100.0 / total));

        return new MissionStatusDto(periodType, total, completed, rate);
    }
}
