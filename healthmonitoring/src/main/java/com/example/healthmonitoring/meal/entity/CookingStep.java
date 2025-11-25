package com.example.healthmonitoring.meal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cooking_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CookingStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(nullable = false)
    private Integer stepNumber; // Bước 1, 2, 3...

    @Column(nullable = false, length = 1000)
    private String description; // Mô tả cách làm

    @Column(length = 500)
    private String imageUrl; // Ảnh minh họa (optional)
}