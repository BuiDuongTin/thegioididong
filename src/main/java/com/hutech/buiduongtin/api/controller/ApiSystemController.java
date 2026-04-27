package com.hutech.buiduongtin.api.controller;

import com.hutech.buiduongtin.api.dto.response.ApiResponse;
import com.hutech.buiduongtin.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class ApiSystemController {

    private final JdbcTemplate jdbcTemplate;
    private final ImageStorageService imageStorageService;

    @GetMapping("/ping")
    public ApiResponse<Map<String, Object>> ping() {
        boolean dbUp = isDatabaseUp();
        boolean imageStorageUp = imageStorageService.isStorageReady();
        String status = dbUp && imageStorageUp ? "UP" : "DEGRADED";

        return ApiResponse.success("OK", Map.of(
                "status", status,
                "database", dbUp ? "UP" : "DOWN",
                "imageStorage", imageStorageUp ? "UP" : "DOWN",
                "imageStorageLocation", imageStorageService.getStorageLocation()));
    }

    private boolean isDatabaseUp() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return result != null && result == 1;
        } catch (Exception ex) {
            return false;
        }
    }
}
