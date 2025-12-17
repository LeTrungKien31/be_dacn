package com.example.healthmonitoring.meal.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.healthmonitoring.meal.entity.MealLog;
import com.example.healthmonitoring.meal.entity.Food;
import com.example.healthmonitoring.meal.repo.FoodRepository;
import com.example.healthmonitoring.meal.repo.MealLogRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/meal")
@RequiredArgsConstructor
public class MealController {

    private final MealLogRepository mealRepo;
    private final FoodRepository foodRepo;

    private String getUserId(Authentication auth) {
        return auth.getName();
    }

    /**
     * FIX: Add meal log with proper food loading
     */
    @PostMapping
@ResponseStatus(HttpStatus.NO_CONTENT) // 204, không body
public void add(@RequestBody CreateReq req, Authentication auth) {
    if (req == null) {
        throw new IllegalArgumentException("Request body is required");
    }
    if (req.foodId == null || req.foodId <= 0) {
        throw new IllegalArgumentException("Valid foodId is required");
    }
    if (req.servings <= 0) {
        throw new IllegalArgumentException("servings must be > 0");
    }

    // Nếu chắc chắn đã đăng nhập:
    if (auth == null || !auth.isAuthenticated()) {
        throw new RuntimeException("Unauthorized");
    }

    Food food = foodRepo.findByIdBasic(req.foodId)
            .orElseThrow(() -> new RuntimeException("Food not found with id: " + req.foodId));

    MealLog log = new MealLog();
    log.setUserId(getUserId(auth));
    log.setFood(food);
    log.setServings(req.servings);
    log.setTotalKcal((int) Math.round(food.getKcalPerServing() * req.servings));

    mealRepo.save(log);
}


    @GetMapping("/today/total")
    public TodayKcalRes todayTotal(Authentication auth) {
        LocalDate today = LocalDate.now();
        var start = today.atStartOfDay();
        var end = today.plusDays(1).atStartOfDay().minusNanos(1);
        int total = mealRepo.sumKcalByUserAndRange(getUserId(auth), start, end);
        return new TodayKcalRes(total);
    }

    @GetMapping("/history")
    public List<MealLog> history(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var start = from.atStartOfDay();
        var end = to.plusDays(1).atStartOfDay().minusNanos(1);
        return mealRepo.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(getUserId(auth), start, end);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        MealLog meal = mealRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal log not found"));
        if (!meal.getUserId().equals(getUserId(auth))) {
            throw new RuntimeException("Forbidden");
        }
        mealRepo.deleteById(id);
    }

    // DTOs
    public static class CreateReq {
        public Long foodId;
        public double servings;
    }

    public static class TodayKcalRes {
        public int totalKcal;
        public TodayKcalRes(int v) {
            totalKcal = v;
        }
    }
}