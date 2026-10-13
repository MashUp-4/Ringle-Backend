package com.ceos24.mashup4backend;

import com.ceos24.mashup4backend.global.response.ApiResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success("서버가 정상 동작 중입니다.", Map.of("status", "ok"));
    }
}
