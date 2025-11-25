package com.example.healthmonitoring.meal.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.healthmonitoring.meal.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByNameIgnoreCase(String name);
    
    /**
     * FIX: Fetch food with ingredients and cooking steps separately to avoid MultipleBagFetchException
     * Use 2 separate queries instead of joining both collections at once
     */
    @Query("SELECT DISTINCT f FROM Food f " +
           "LEFT JOIN FETCH f.ingredients " +
           "WHERE f.id = :id")
    Optional<Food> findByIdWithIngredients(@Param("id") Long id);
    
    @Query("SELECT DISTINCT f FROM Food f " +
           "LEFT JOIN FETCH f.cookingSteps " +
           "WHERE f.id = :id")
    Optional<Food> findByIdWithSteps(@Param("id") Long id);
    
    /**
     * Alternative: Use @EntityGraph to fetch all at once
     */
    @Query("SELECT f FROM Food f WHERE f.id = :id")
    @org.springframework.data.jpa.repository.EntityGraph(
        attributePaths = {"ingredients", "cookingSteps"}
    )
    Optional<Food> findByIdWithDetails(@Param("id") Long id);
}