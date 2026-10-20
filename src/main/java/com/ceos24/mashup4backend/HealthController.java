package com.ceos24.mashup4backend;

import com.ceos24.mashup4backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "서버 상태", description = "서버 동작 상태 확인 API")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "서버 상태 확인", description = "서버가 응답 가능한 상태이면 data.status에 ok를 반환합니다.")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success("서버가 정상 동작 중입니다.", Map.of("status", "ok"));
    }
}
