package com.ll.finhabit.domain.mission.controller;

import com.ll.finhabit.domain.mission.dto.MissionArchiveResponse;
import com.ll.finhabit.domain.mission.dto.MissionProgressDto;
import com.ll.finhabit.domain.mission.dto.MissionTodayResponse;
import com.ll.finhabit.domain.mission.service.MissionService;
import com.ll.finhabit.global.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
@Tag(name = "Mission", description = "오늘의 미션 / 미션 체크 / 아카이브 조회 API")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/today")
    @Operation(
            summary = "오늘의 미션 조회",
            description =
                    """
                    오늘 날짜 기준으로 **로그인한 사용자의 미션**을 조회합니다.<br>
                    - 이미 오늘 배정된 미션이 있다면 그대로 반환합니다.<br>
                    - 미배정 상태라면 유저 레벨 이하의 미션 중 이번 주 소진되지 않은 미션을 랜덤으로 배정합니다.<br>
                    - 이번 주에 더 이상 배정 가능한 미션이 없다면 todayMission 값은 null로 반환됩니다.
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "오늘의 미션 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저")
    })
    public MissionTodayResponse getTodayMission(
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return missionService.getMissionToday(userId);
    }

    @PostMapping("/{userMissionId}/check")
    @Operation(
            summary = "미션 수행 체크",
            description =
                    """
                    로그인한 사용자의 유저 미션 진행도를 1 증가시키는 API입니다.<br>
                    - 진행도가 totalCount 이상으로 넘어가지 않도록 제한됩니다.<br>
                    - 진행도가 totalCount에 도달하면 자동으로 완료 상태(isCompleted=true)로 변경되고, completedAt에 날짜가 기록됩니다.<br>
                    - 이미 완료된 상태라면 그대로 반환됩니다.<br>
                    - 동시에 여러 요청이 들어와 충돌(낙관적 락)이 발생하면 409(CONFLICT)를 반환합니다.
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "미션 체크 성공"),
        @ApiResponse(responseCode = "403", description = "해당 미션에 대한 권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 미션"),
        @ApiResponse(responseCode = "409", description = "동시성 충돌 발생")
    })
    public MissionProgressDto checkMission(
            @Parameter(hidden = true) @CurrentUser Long userId, @PathVariable Long userMissionId) {
        return missionService.checkMission(userId, userMissionId);
    }

    @PostMapping("/{userMissionId}/uncheck")
    @Operation(
            summary = "미션 체크 취소",
            description =
                    """
                    로그인한 사용자의 유저 미션 진행도를 1 감소시키는 API입니다.<br>
                    - 진행도가 0보다 작아지지 않도록 제한됩니다.<br>
                    - 완료 상태였다가 진행도가 totalCount 미만으로 내려가면 자동으로 완료 상태가 해제되고 completedAt 값이 비워집니다.<br>
                    - 동시에 여러 요청이 들어오면 409(CONFLICT) 충돌 응답이 발생합니다.
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "미션 체크 취소 성공"),
        @ApiResponse(responseCode = "403", description = "해당 미션에 대한 권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 미션"),
        @ApiResponse(responseCode = "409", description = "동시성 충돌 발생")
    })
    public MissionProgressDto undoMissionCheck(
            @Parameter(hidden = true) @CurrentUser Long userId, @PathVariable Long userMissionId) {
        return missionService.undoMissionCheck(userId, userMissionId);
    }

    @GetMapping("/archive")
    @Operation(
            summary = "미션 아카이브 조회",
            description =
                    """
                    로그인한 사용자의 완료된 미션들을 주차별(weekStart)로 묶어 반환합니다.<br>
                    - 각 주차는 월요일(weekStart)부터 일요일(weekEnd)까지의 범위로 그룹화됩니다.<br>
                    - 최신 주차가 먼저 오도록 내림차순 정렬하여 반환합니다.
                    """)
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "미션 아카이브 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저")
    })
    public List<MissionArchiveResponse> getMissionArchive(
            @Parameter(hidden = true) @CurrentUser Long userId) {
        return missionService.getMissionArchive(userId);
    }
}
