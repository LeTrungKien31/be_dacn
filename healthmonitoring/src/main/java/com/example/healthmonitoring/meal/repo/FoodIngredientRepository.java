package com.example.healthmonitoring.meal.repo;

import com.example.healthmonitoring.meal.entity.FoodIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface FoodIngredientRepository extends JpaRepository<FoodIngredient, Long> {
    
    List<FoodIngredient> findByFoodId(Long foodId);
    
    List<FoodIngredient> findByIngredientId(Long ingredientId);
    
    boolean existsByFoodIdAndIngredientId(Long foodId, Long ingredientId);
    
    void deleteByFoodId(Long foodId);
    
    @Query("SELECT COUNT(fi) FROM FoodIngredient fi WHERE fi.food.id = ?1")
    long countByFoodId(Long foodId);
}