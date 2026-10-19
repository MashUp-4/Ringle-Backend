package com.ceos24.mashup4backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record ProfileImageResponse(
        String imageUrl
) {
}
