package com.example.healthmonitoring.meal.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.healthmonitoring.meal.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByNameIgnoreCase(String name);
    
    /**
     * FIX: Load Food without any collections for list view
     */
    @Query("SELECT f FROM Food f WHERE f.id = :id")
    Optional<Food> findByIdBasic(@Param("id") Long id);
    
    /**
     * FIX: Load Food with details using separate queries
     */
    @Query("SELECT f FROM Food f WHERE f.id = :id")
    @org.springframework.data.jpa.repository.EntityGraph(
        attributePaths = {"ingredients", "cookingSteps"}
    )
    Optional<Food> findByIdWithDetails(@Param("id") Long id);
}