package com.example.healthmonitoring.meal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String serving;

    @Column(nullable = false)
    private int kcalPerServing;

    @Column
    private Double carbs;

    @Column
    private Double protein;

    @Column
    private Double fat;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 2000)
    private String description; // Mô tả món ăn

    // Quan hệ với Ingredient
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<Ingredient> ingredients = new ArrayList<>();

    // Quan hệ với CookingStep
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<CookingStep> cookingSteps = new ArrayList<>();
}