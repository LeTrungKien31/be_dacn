package com.example.healthmonitoring.meal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private String description;

    // FIX: Use LAZY fetch and JsonManagedReference to avoid circular references
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @JsonManagedReference("food-ingredients")
    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepNumber ASC")
    @JsonManagedReference("food-steps")
    @Builder.Default
    private List<CookingStep> cookingSteps = new ArrayList<>();
}