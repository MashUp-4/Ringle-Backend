package com.ceos24.mashup4backend.domain.user.controller;

import com.ceos24.mashup4backend.domain.tutor.service.TutorFavoriteService;
import com.ceos24.mashup4backend.global.response.ApiResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final TutorFavoriteService tutorFavoriteService;

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
}
