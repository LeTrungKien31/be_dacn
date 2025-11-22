package com.example.healthmonitoring.bmi.controller;

import com.example.healthmonitoring.bmi.entity.WeightLog;
import com.example.healthmonitoring.bmi.service.BmiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bmi")
@RequiredArgsConstructor
public class BmiController {

    private final BmiService bmiService;

    private String getUserId(Authentication auth) {
        return auth.getName();
    }

    /**
     * Log weight
     */
    @PostMapping("/weight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightLog logWeight(@Valid @RequestBody LogWeightRequest request, Authentication auth) {
        return bmiService.logWeight(getUserId(auth), request.getWeightKg(), request.getNote());
    }

    /**
     * Get weight history
     */
    @GetMapping("/weight/history")
    public List<WeightLog> getWeightHistory(Authentication auth) {
        return bmiService.getWeightHistory(getUserId(auth));
    }

    /**
     * Get latest weight
     */
    @GetMapping("/weight/latest")
    public WeightLog getLatestWeight(Authentication auth) {
        return bmiService.getLatestWeight(getUserId(auth));
    }

    /**
     * Get weight progress for chart
     */
    @GetMapping("/weight/progress")
    public BmiService.WeightProgressResponse getWeightProgress(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return bmiService.getWeightProgress(getUserId(auth), from, to);
    }

    /**
     * Delete weight log
     */
    @DeleteMapping("/weight/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeightLog(@PathVariable Long id, Authentication auth) {
        bmiService.deleteWeightLog(getUserId(auth), id);
    }

    // DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogWeightRequest {
        @Positive(message = "Weight must be positive")
        @DecimalMin(value = "20", message = "Weight must be at least 20kg")
        @DecimalMax(value = "500", message = "Weight must not exceed 500kg")
        private double weightKg;

        private String note;
    }
}