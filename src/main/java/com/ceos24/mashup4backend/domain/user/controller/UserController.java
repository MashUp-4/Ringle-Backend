package com.ceos24.mashup4backend.domain.user.controller;

import com.ceos24.mashup4backend.domain.tutor.service.TutorFavoriteService;
import com.ceos24.mashup4backend.domain.user.dto.ProfileImageResponse;
import com.ceos24.mashup4backend.domain.user.dto.UserSummaryResponse;
import com.ceos24.mashup4backend.domain.user.service.UserService;
import com.ceos24.mashup4backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 정보 및 튜터 북마크 API. 현재 사용자 ID는 1로 고정되어 있습니다.")
public class UserController {

    private final TutorFavoriteService tutorFavoriteService;
    private final UserService userService;

    @GetMapping("/me/summary")
    @Operation(summary = "유저 회원 정보 요약 조회", description = "이름, 시간대, 수업권, 포인트, 미사용 쿠폰 수를 조회합니다.")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getSummary() {
        Long userId = 1L;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("회원 정보를 조회했습니다.", userService.getSummary(userId)));
    }

    @GetMapping("/me/profile-image")
    @Operation(summary = "유저 프로필 사진 조회", description = "프로필 사진 URL을 조회합니다. 등록된 사진이 없으면 imageUrl은 null입니다.")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> getProfileImage() {
        Long userId = 1L;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 사진을 조회했습니다.", userService.getProfileImage(userId)));
    }

    @PostMapping("/me/favorite-tutors/{tutorId}")
    @Operation(summary = "튜터 북마크 등록", description = "선택한 튜터를 내 북마크에 추가합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "튜터 북마크 등록 성공")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @PathVariable("tutorId") @Positive Long tutorId
    ) {
        Long userId = 1L;
        tutorFavoriteService.addFavorite(userId, tutorId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("FAVORITE_TUTOR_CREATED", "튜터를 북마크했습니다.", null));
    }

    @DeleteMapping("/me/favorite-tutors/{tutorId}")
    @Operation(summary = "튜터 북마크 해제", description = "선택한 튜터를 내 북마크에서 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable("tutorId") @Positive Long tutorId
    ) {
        Long userId = 1L;
        tutorFavoriteService.removeFavorite(userId, tutorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("FAVORITE_TUTOR_REMOVED", "튜터 북마크를 해제했습니다.", null));
    }
}
