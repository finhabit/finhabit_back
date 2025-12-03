package com.ll.finhabit.domain.mission.service;

import com.ll.finhabit.domain.mission.dto.MissionArchiveResponse;
import com.ll.finhabit.domain.mission.dto.MissionProgressDto;
import com.ll.finhabit.domain.mission.dto.MissionTodayResponse;
import com.ll.finhabit.domain.mission.entity.UserMission;
import com.ll.finhabit.domain.mission.repository.UserMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final UserMissionRepository userMissionRepository;

    // 오늘의 미션 + 이번주 미완료 미션 전체
    public MissionTodayResponse getMissionToday(Long userId) {

        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);

        // 이번 주 + 미완료 미션들
        List<UserMission> ongoingMissions =
                userMissionRepository.findByUser_UserIdAndWeekStartAndIsCompletedFalseOrderByUsermissionIdAsc(
                        userId, thisMonday
                );

        // 이번 주 + 미완료 첫 번째 → "오늘의 미션"
        UserMission today = userMissionRepository
                .findFirstByUser_UserIdAndWeekStartAndIsCompletedFalseOrderByUsermissionIdAsc(
                        userId, thisMonday
                )
                .orElse(null);

        MissionProgressDto todayDto = (today == null) ? null : toDto(today);

        List<MissionProgressDto> ongoingDtos = ongoingMissions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return MissionTodayResponse.builder()
                .todayMission(todayDto)
                .ongoingMissions(ongoingDtos)
                .build();
    }

    // 미션 체크(1회 수행)
    @Transactional
    public MissionProgressDto checkMission(Long userId, Long userMissionId) {

        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미션입니다."));

        if (!userMission.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 미션을 수행할 권한이 없습니다.");
        }

        // 이미 완료면 그대로 반환
        if (userMission.getIsCompleted()) {
            return toDto(userMission);
        }

        // doneCount 증가
        int newCount = userMission.getDoneCount() + 1;
        int total = userMission.getMission().getTotalCount();

        if (newCount > total) newCount = total;

        userMission.setDoneCount(newCount);

        // 진행률 계산
        int progress = (int) Math.round((newCount * 100.0) / total);
        userMission.setProgress(progress);

        // 완료 처리
        if (newCount >= total) {
            userMission.setIsCompleted(true);
            userMission.setCompletedAt(LocalDate.now());
        }

        return toDto(userMission);
    }

    // 완료 아카이브 (주별)
    public List<MissionArchiveResponse> getMissionArchive(Long userId) {

        // DB에서 "완료된 미션"을 모두 가져오면 됨 — 주별로 이미 weekStart가 있으니
        List<UserMission> completed = userMissionRepository
                .findAll()
                .stream()
                .filter(um -> um.getUser().getUserId().equals(userId))
                .filter(um -> um.getIsCompleted() != null && um.getIsCompleted())
                .collect(Collectors.toList());

        if (completed.isEmpty()) return Collections.emptyList();

        // weekStart 기준으로 그룹핑
        Map<LocalDate, List<UserMission>> byWeek =
                completed.stream()
                        .collect(Collectors.groupingBy(UserMission::getWeekStart));

        // 최근 주 먼저 정렬
        return byWeek.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<UserMission>>comparingByKey().reversed())
                .map(entry -> {
                    LocalDate weekStart = entry.getKey();
                    LocalDate weekEnd = weekStart.plusDays(6);

                    List<MissionProgressDto> missionDtos = entry.getValue().stream()
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

    // 공통 변환
    private MissionProgressDto toDto(UserMission um) {
        int totalCount = um.getMission().getTotalCount();
        int done = um.getDoneCount();

        int progress = (int) Math.round((done * 100.0) / totalCount);

        return MissionProgressDto.builder()
                .userMissionId(um.getUsermissionId())
                .missionId(um.getMission().getMissionId())
                .missionContent(um.getMission().getMissionContent())
                .missionLevel(um.getMission().getMissionLevel())
                .totalCount(totalCount)
                .doneCount(done)
                .progress(progress)
                .completed(um.getIsCompleted())
                .build();
    }
}
