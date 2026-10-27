package com.ceos24.mashup4backend.domain.user.controller;

import com.ceos24.mashup4backend.domain.learning.entity.LearningGoal;
import com.ceos24.mashup4backend.domain.user.dto.UserHomeResponse;
import com.ceos24.mashup4backend.domain.user.service.UserHomeService;
import com.ceos24.mashup4backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
@Tag(name = "회원")
public class UserHomeController {

    private final UserHomeService userHomeService;

    @GetMapping("/home")
    @Operation(summary = "홈 화면 조회", description = "예정된 수업이 있으면 수업 목록을, 없으면 추천 튜터를 최대 3명 반환합니다. "
            + "시간은 한국 시간 기준이며 현재 사용자 ID는 1로 고정되어 있습니다.")
    public ApiResponse<UserHomeResponse> getHome(
            @Parameter(description = "추천 학습 목적. 생략하면 가입 시 선택한 학습 목적을 사용합니다.")
            @RequestParam(name = "category", required = false) LearningGoal category
    ) {
        Long userId = 1L;
        return ApiResponse.success("홈 화면을 조회했습니다.", userHomeService.getHome(userId, category));
    }
}
