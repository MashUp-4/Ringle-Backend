package com.ceos24.mashup4backend.domain.user.controller;

import com.ceos24.mashup4backend.domain.tutor.service.TutorFavoriteService;
import com.ceos24.mashup4backend.domain.user.dto.ProfileImageResponse;
import com.ceos24.mashup4backend.domain.user.service.UserService;
import com.ceos24.mashup4backend.global.response.ApiResponse;
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
public class UserController {

    private final TutorFavoriteService tutorFavoriteService;
    private final UserService userService;

    @GetMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> getProfileImage() {
        Long userId = 1L;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("프로필 사진을 조회했습니다.", userService.getProfileImage(userId)));
    }

    @PostMapping("/me/favorite-tutors/{tutorId}")
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
