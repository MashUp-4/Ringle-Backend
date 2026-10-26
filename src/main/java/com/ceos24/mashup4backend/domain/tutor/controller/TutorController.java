package com.ceos24.mashup4backend.domain.tutor.controller;

import com.ceos24.mashup4backend.domain.tutor.dto.TutorListResponse;
import com.ceos24.mashup4backend.domain.tutor.dto.TutorSearchRequest;
import com.ceos24.mashup4backend.domain.tutor.service.TutorService;
import com.ceos24.mashup4backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tutors")
@RequiredArgsConstructor
@Tag(name = "튜터", description = "튜터 조회 API")
public class TutorController {

    private final TutorService tutorService;

    @GetMapping
    @Operation(summary = "튜터 목록 조회")
    public ApiResponse<TutorListResponse> getTutors(
            @ParameterObject @ModelAttribute TutorSearchRequest request
    ) {
        // 현재 프로젝트의 다른 API와 동일한 임시 사용자
        Long userId = 1L;

        TutorListResponse response = tutorService.getTutors(userId, request);

        return ApiResponse.success(
                "튜터 목록을 조회했습니다.",
                response
        );
    }
}
