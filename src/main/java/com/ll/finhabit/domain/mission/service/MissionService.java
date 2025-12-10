package com.ll.finhabit.domain.mission.service;

import com.ll.finhabit.domain.auth.repository.UserRepository;
import com.ll.finhabit.domain.mission.dto.MissionArchiveResponse;
import com.ll.finhabit.domain.mission.dto.MissionProgressDto;
import com.ll.finhabit.domain.mission.dto.MissionTodayResponse;
import com.ll.finhabit.domain.mission.entity.Mission;
import com.ll.finhabit.domain.mission.entity.UserMission;
import com.ll.finhabit.domain.mission.repository.MissionRepository;
import com.ll.finhabit.domain.mission.repository.UserMissionRepository;
import jakarta.persistence.OptimisticLockException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;

    private int calculateProgress(int doneCount, int totalCount) {
        if (totalCount <= 0) return 0;
        return (int) Math.round(doneCount * 100.0 / totalCount);
    }

    // 오늘의 미션 가져오기
    @Transactional
    public MissionTodayResponse getMissionToday(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(DayOfWeek.MONDAY);

        try {
            // 1. 먼저 조회
            UserMission todayMission =
                    userMissionRepository.findByUser_IdAndAssignedDate(userId, today).orElse(null);

            if (todayMission != null) {
                return MissionTodayResponse.builder().todayMission(toDto(todayMission)).build();
            }

            // 2. 없으면 생성
            var user =
                    userRepository
                            .findById(userId)
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

            int userLevel = user.getLevel();

            List<Mission> allMissions =
                    missionRepository.findByMissionLevelLessThanEqual(userLevel);

            List<UserMission> thisWeekMissions =
                    userMissionRepository.findByUser_IdAndWeekStart(userId, thisMonday);

            // missionId 기준으로 이번 주 배정 횟수 카운팅
            Map<Long, Long> assignedCountByMissionId =
                    thisWeekMissions.stream()
                            .collect(
                                    Collectors.groupingBy(
                                            um -> um.getMission().getMissionId(),
                                            Collectors.counting()));

            // 이번 주에 아직 여유가 남아 있는 미션만 후보로 필터링
            List<Mission> candidates =
                    allMissions.stream()
                            .filter(
                                    mission -> {
                                        long assignedThisWeek =
                                                assignedCountByMissionId.getOrDefault(
                                                        mission.getMissionId(), 0L);
                                        return assignedThisWeek < mission.getTotalCount();
                                    })
                            .toList();

            if (candidates.isEmpty()) {
                return MissionTodayResponse.builder().todayMission(null).build();
            }

            // 랜덤 선택 + UserMission 생성
            int idx = ThreadLocalRandom.current().nextInt(candidates.size());
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

        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 중복 생성 시도 시, 다시 조회해서 반환
            UserMission todayMission =
                    userMissionRepository
                            .findByUser_IdAndAssignedDate(userId, LocalDate.now())
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.INTERNAL_SERVER_ERROR, "미션 조회 실패"));

            return MissionTodayResponse.builder().todayMission(toDto(todayMission)).build();
        }
    }

    // 미션 수행 체크 버튼
    @Transactional
    public MissionProgressDto checkMission(Long userId, Long userMissionId) {
        try {
            UserMission userMission =
                    userMissionRepository
                            .findById(userMissionId)
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND, "존재하지 않는 유저 미션입니다."));

            // 소유자 검사
            if (!userMission.getUser().getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 미션에 대한 권한이 없습니다.");
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
            userMission.setProgress(calculateProgress(newDoneCount, totalCount));

            // 완료 처리
            if (newDoneCount >= totalCount) {
                userMission.setIsCompleted(true);
                userMission.setCompletedAt(LocalDate.now());
            }
            userMissionRepository.flush();

            // JPA가 커밋 시 자동으로 version 체크
            return toDto(userMission);

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            // 동시 요청 감지 시
            throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 요청과 충돌했습니다. 다시 시도해주세요.");
        }
    }

    // 수행 버튼 취소
    @Transactional
    public MissionProgressDto undoMissionCheck(Long userId, Long userMissionId) {
        try {
            UserMission userMission =
                    userMissionRepository
                            .findById(userMissionId)
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND, "존재하지 않는 유저 미션입니다."));

            // 소유자 검사
            if (!userMission.getUser().getId().equals(userId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 미션에 대한 권한이 없습니다.");
            }

            int currentDone = userMission.getDoneCount();

            // 이미 0이면 더 줄일 수 없음
            if (currentDone <= 0) {
                return toDto(userMission);
            }

            int newDoneCount = currentDone - 1;
            int totalCount = userMission.getMission().getTotalCount();

            userMission.setDoneCount(newDoneCount);
            userMission.setProgress(calculateProgress(newDoneCount, totalCount));

            // 완료 취소시 완료 상태 false로 변경
            if (newDoneCount < totalCount) {
                userMission.setIsCompleted(false);
                userMission.setCompletedAt(null);
            }
            userMissionRepository.flush();

            // JPA가 커밋 시 자동으로 version 체크
            return toDto(userMission);

        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            // 동시 요청 감지 시
            throw new ResponseStatusException(HttpStatus.CONFLICT, "다른 요청과 충돌했습니다. 다시 시도해주세요.");
        }
    }

    // 미션 완료 아카이브
    @Transactional(readOnly = true)
    public List<MissionArchiveResponse> getMissionArchive(Long userId) {

        var user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        List<UserMission> completed =
                userMissionRepository.findByUser_IdAndIsCompletedTrueAndWeekStartIsNotNull(userId);

        if (completed.isEmpty()) {
            return Collections.emptyList();
        }

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

    // DTO 변환 메서드
    private MissionProgressDto toDto(UserMission userMission) {

        int totalCount = userMission.getMission().getTotalCount();
        int doneCount = userMission.getDoneCount();
        int progress = calculateProgress(doneCount, totalCount);

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
