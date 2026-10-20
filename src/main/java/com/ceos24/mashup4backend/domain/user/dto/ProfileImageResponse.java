package com.ceos24.mashup4backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(description = "회원 프로필 사진 응답")
public record ProfileImageResponse(
        @Schema(description = "프로필 사진 URL. 등록된 사진이 없으면 null", example = "https://example.com/profile/1.jpg")
        String imageUrl
) {
}
