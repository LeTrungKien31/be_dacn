package com.example.healthmonitoring.meal.repo;

import com.example.healthmonitoring.meal.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByFoodIdOrderByDisplayOrderAsc(Long foodId);
}