package com.ll.finhabit.domain.mission.service;

import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.mission.dto.MissionArchiveResponse;
import com.ll.finhabit.domain.mission.dto.MissionProgressDto;
import com.ll.finhabit.domain.mission.dto.MissionTodayResponse;
import com.ll.finhabit.domain.mission.entity.Mission;
import com.ll.finhabit.domain.mission.entity.UserMission;
import com.ll.finhabit.domain.mission.repository.MissionRepository;
import com.ll.finhabit.domain.mission.repository.UserMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    // 오늘의 미션 가져오기
    @Transactional(readOnly = true)
    public MissionTodayResponse getMissionToday(Long userId) {

        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);

        // 오늘 이미 배정된 미션 있으면 그대로 반환
        UserMission todayMission =
                userMissionRepository.findByUser_IdAndAssignedDate(userId, today).orElse(null);

        if (todayMission != null) {
            return MissionTodayResponse.builder().todayMission(toDto(todayMission)).build();
        }

        // 유저 가져오기
        var user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        int userLevel = user.getLevel();

        List<Mission> allMissions = missionRepository.findByMissionLevelLessThanEqual(userLevel);

        // 이번 주에 횟수가 남은 미션만 후보로 필터링
        List<Mission> candidates =
                allMissions.stream()
                        .filter(
                                mission -> {
                                    long assignedThisWeek =
                                            userMissionRepository
                                                    .countByUser_IdAndMission_MissionIdAndWeekStart(
                                                            userId,
                                                            mission.getMissionId(),
                                                            thisMonday);
                                    // 각 미션별 totalCount 이하까지만 허용
                                    return assignedThisWeek < mission.getTotalCount();
                                })
                        .toList();

        if (candidates.isEmpty()) {
            return MissionTodayResponse.builder().todayMission(null).build();
        }

        // 랜덤 선택 + UserMission 생성
        int idx = new Random().nextInt(candidates.size());
        Mission chosen = candidates.get(idx);

        UserMission newUserMission =
                UserMission.builder()
                        .user(user)
                        .mission(chosen)
                        .isCompleted(false)
                        .doneCount(0)
                        .progress(0)
                        .weekStart(thisMonday)
                        .assignedDate(today)
                        .completedAt(null)
                        .build();

        todayMission = userMissionRepository.save(newUserMission);

        return MissionTodayResponse.builder().todayMission(toDto(todayMission)).build();
    }

    // 미션 수행 체크 버튼
    @Transactional
    public MissionProgressDto checkMission(Long userId, Long userMissionId) {

        UserMission userMission =
                userMissionRepository
                        .findById(userMissionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 미션입니다."));

        // 소유자 검사
        if (!userMission.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 미션에 대한 권한이 없습니다.");
        }

        // 이미 완료된 경우 그대로 반환
        if (Boolean.TRUE.equals(userMission.getIsCompleted())) {
            return toDto(userMission);
        }

        int newDoneCount = userMission.getDoneCount() + 1;
        int totalCount = userMission.getMission().getTotalCount();

        if (newDoneCount > totalCount) {
            newDoneCount = totalCount;
        }

        userMission.setDoneCount(newDoneCount);

        int progress = (int) Math.round((newDoneCount * 100.0) / totalCount);
        userMission.setProgress(progress);

        // 완료 처리
        if (newDoneCount >= totalCount) {
            userMission.setIsCompleted(true);
            userMission.setCompletedAt(LocalDate.now());
        }

        return toDto(userMission);
    }

    // 수행 버튼 취소
    @Transactional
    public MissionProgressDto undoMissionCheck(Long userId, Long userMissionId) {

        UserMission userMission =
                userMissionRepository
                        .findById(userMissionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 미션입니다."));

        // 소유자 검사
        if (!userMission.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 미션에 대한 권한이 없습니다.");
        }

        int currentDone = userMission.getDoneCount();

        // 이미 0이면 더 줄일 수 없음
        if (currentDone <= 0) {
            return toDto(userMission);
        }

        int newDoneCount = currentDone - 1;
        int totalCount = userMission.getMission().getTotalCount();

        userMission.setDoneCount(newDoneCount);

        int progress = (int) Math.round((newDoneCount * 100.0) / totalCount);
        userMission.setProgress(progress);

        // 완료 취소시 완료 상태 false로 변경
        if (newDoneCount < totalCount) {
            userMission.setIsCompleted(false);
            userMission.setCompletedAt(null);
        }

        return toDto(userMission);
    }

    // 미션 완료 아카이브
    @Transactional(readOnly = true)
    public List<MissionArchiveResponse> getMissionArchive(Long userId) {

        List<UserMission> completed =
                userMissionRepository.findAll().stream()
                        .filter(um -> um.getUser().getId().equals(userId))
                        .filter(um -> Boolean.TRUE.equals(um.getIsCompleted()))
                        .filter(um -> um.getWeekStart() != null)
                        .collect(Collectors.toList());

        if (completed.isEmpty()) return Collections.emptyList();

        // 주별 그룹핑
        Map<LocalDate, List<UserMission>> byWeekStart =
                completed.stream().collect(Collectors.groupingBy(UserMission::getWeekStart));

        // 최신 주부터 반환
        return byWeekStart.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<UserMission>>comparingByKey().reversed())
                .map(
                        entry -> {
                            LocalDate weekStart = entry.getKey();
                            LocalDate weekEnd = weekStart.plusDays(6);

                            List<MissionProgressDto> missionDtos =
                                    entry.getValue().stream()
                                            .map(this::toDto)
                                            .collect(Collectors.toList());

                            return MissionArchiveResponse.builder()
                                    .weekStart(weekStart)
                                    .weekEnd(weekEnd)
                                    .missions(missionDtos)
                                    .build();
                        })
                .collect(Collectors.toList());
    }

    // 디티오 메서드
    private MissionProgressDto toDto(UserMission userMission) {

        int totalCount = userMission.getMission().getTotalCount();
        int doneCount = userMission.getDoneCount();

        int progress = (totalCount == 0) ? 0 : (int) Math.round(doneCount * 100.0 / totalCount);

        return MissionProgressDto.builder()
                .userMissionId(userMission.getUsermissionId())
                .missionId(userMission.getMission().getMissionId())
                .missionContent(userMission.getMission().getMissionContent())
                .missionLevel(userMission.getMission().getMissionLevel())
                .totalCount(totalCount)
                .doneCount(doneCount)
                .progress(progress)
                .completed(userMission.getIsCompleted())
                .build();
    }
}
