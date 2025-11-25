package com.example.healthmonitoring.meal.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.healthmonitoring.meal.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByNameIgnoreCase(String name);
    
    /**
     * Fetch food with ingredients and cooking steps in one query
     * This avoids N+1 query problem
     */
    @Query("SELECT f FROM Food f " +
           "LEFT JOIN FETCH f.ingredients " +
           "LEFT JOIN FETCH f.cookingSteps " +
           "WHERE f.id = :id")
    Optional<Food> findByIdWithDetails(@Param("id") Long id);
}