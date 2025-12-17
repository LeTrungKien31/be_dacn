package com.example.healthmonitoring.meal.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.healthmonitoring.meal.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByNameIgnoreCase(String name);

    // Nếu thích có method riêng:
    @Query("SELECT f FROM Food f WHERE f.id = :id")
    Optional<Food> findByIdBasic(@Param("id") Long id);
}
