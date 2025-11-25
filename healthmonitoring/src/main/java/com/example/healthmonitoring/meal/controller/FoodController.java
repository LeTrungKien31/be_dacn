package com.example.healthmonitoring.meal.controller;

import java.util.List;
import java.util.stream.Collectors;

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
     * Trả về danh sách đơn giản không bao gồm ingredients và cookingSteps
     */
    @GetMapping
    public List<FoodListDTO> list(@RequestParam(required = false) String q) {
        List<Food> foods;
        if (q == null || q.isBlank()) {
            foods = repo.findAll();
        } else {
            final var lower = q.toLowerCase();
            foods = repo.findAll().stream()
                    .filter(f -> f.getName().toLowerCase().contains(lower))
                    .toList();
        }
        
        // Convert to DTO to avoid lazy loading issues
        return foods.stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết món ăn với nguyên liệu và cách nấu
     * Sử dụng findByIdWithDetails để fetch tất cả relations trong 1 query
     */
    @GetMapping("/{id}")
    public FoodDetailDTO getDetail(@PathVariable Long id) {
        Food food = repo.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
        
        return toDetailDTO(food);
    }

    // Helper methods to convert to DTOs
    private FoodListDTO toListDTO(Food food) {
        FoodListDTO dto = new FoodListDTO();
        dto.setId(food.getId());
        dto.setName(food.getName());
        dto.setServing(food.getServing());
        dto.setKcalPerServing(food.getKcalPerServing());
        dto.setCarbs(food.getCarbs());
        dto.setProtein(food.getProtein());
        dto.setFat(food.getFat());
        dto.setImageUrl(food.getImageUrl());
        dto.setDescription(food.getDescription());
        return dto;
    }

    private FoodDetailDTO toDetailDTO(Food food) {
        FoodDetailDTO dto = new FoodDetailDTO();
        dto.setId(food.getId());
        dto.setName(food.getName());
        dto.setServing(food.getServing());
        dto.setKcalPerServing(food.getKcalPerServing());
        dto.setCarbs(food.getCarbs());
        dto.setProtein(food.getProtein());
        dto.setFat(food.getFat());
        dto.setImageUrl(food.getImageUrl());
        dto.setDescription(food.getDescription());
        
        // Convert ingredients
        if (food.getIngredients() != null) {
            dto.setIngredients(food.getIngredients().stream()
                    .map(ing -> {
                        IngredientDTO ingDTO = new IngredientDTO();
                        ingDTO.setId(ing.getId());
                        ingDTO.setName(ing.getName());
                        ingDTO.setQuantity(ing.getQuantity());
                        ingDTO.setDisplayOrder(ing.getDisplayOrder());
                        return ingDTO;
                    })
                    .collect(Collectors.toList()));
        }
        
        // Convert cooking steps
        if (food.getCookingSteps() != null) {
            dto.setCookingSteps(food.getCookingSteps().stream()
                    .map(step -> {
                        CookingStepDTO stepDTO = new CookingStepDTO();
                        stepDTO.setId(step.getId());
                        stepDTO.setStepNumber(step.getStepNumber());
                        stepDTO.setDescription(step.getDescription());
                        stepDTO.setImageUrl(step.getImageUrl());
                        return stepDTO;
                    })
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // DTOs
    @lombok.Data
    public static class FoodListDTO {
        private Long id;
        private String name;
        private String serving;
        private int kcalPerServing;
        private Double carbs;
        private Double protein;
        private Double fat;
        private String imageUrl;
        private String description;
    }

    @lombok.Data
    public static class FoodDetailDTO {
        private Long id;
        private String name;
        private String serving;
        private int kcalPerServing;
        private Double carbs;
        private Double protein;
        private Double fat;
        private String imageUrl;
        private String description;
        private List<IngredientDTO> ingredients;
        private List<CookingStepDTO> cookingSteps;
    }

    @lombok.Data
    public static class IngredientDTO {
        private Long id;
        private String name;
        private String quantity;
        private Integer displayOrder;
    }

    @lombok.Data
    public static class CookingStepDTO {
        private Long id;
        private Integer stepNumber;
        private String description;
        private String imageUrl;
    }
}