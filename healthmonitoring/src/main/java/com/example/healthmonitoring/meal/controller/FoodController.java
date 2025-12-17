package com.example.healthmonitoring.meal.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import com.example.healthmonitoring.meal.entity.Food;
import com.example.healthmonitoring.meal.repo.FoodRepository;

import lombok.RequiredArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodRepository repo;

    /**
     * List foods without loading collections
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

        return foods.stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get detail with proper transaction and collection loading
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public FoodDetailDTO getDetail(@PathVariable Long id) {
        Food food = repo.findById(id) // hoặc findByIdBasic(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));

        // Lúc bạn gọi food.getIngredients() / getCookingSteps()
        // Hibernate sẽ lazy-load bằng 2 câu SELECT riêng → không còn
        // MultipleBagFetchException.
        return toDetailDTO(food);
    }

    /**
     * Convert to list DTO (no collections)
     */
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

    /**
     * Convert to detail DTO (with collections)
     */
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

        // Convert ingredients (lazy loaded)
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

        // Convert cooking steps (lazy loaded)
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

    // ========== DTOs ==========
    @Data
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

    @Data
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

    @Data
    public static class IngredientDTO {
        private Long id;
        private String name;
        private String quantity;
        private Integer displayOrder;
    }

    @Data
    public static class CookingStepDTO {
        private Long id;
        private Integer stepNumber;
        private String description;
        private String imageUrl;
    }
}