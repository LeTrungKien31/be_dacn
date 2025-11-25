package com.example.healthmonitoring.meal.repo;

import com.example.healthmonitoring.meal.entity.CookingStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CookingStepRepository extends JpaRepository<CookingStep, Long> {
    List<CookingStep> findByFoodIdOrderByStepNumberAsc(Long foodId);
}