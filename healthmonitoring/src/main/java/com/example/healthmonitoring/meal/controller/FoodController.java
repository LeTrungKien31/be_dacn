package com.example.healthmonitoring.meal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.healthmonitoring.meal.entity.Food;
import com.example.healthmonitoring.meal.repo.FoodRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodRepository repo;

    /**
     * Lấy danh sách món ăn (tìm kiếm)
     */
    @GetMapping
    public List<Food> list(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        final var lower = q.toLowerCase();
        return repo.findAll().stream()
                .filter(f -> f.getName().toLowerCase().contains(lower))
                .toList();
    }

    /**
     * Lấy chi tiết món ăn với nguyên liệu và cách nấu
     */
    @GetMapping("/{id}")
    public Food getDetail(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
    }
}