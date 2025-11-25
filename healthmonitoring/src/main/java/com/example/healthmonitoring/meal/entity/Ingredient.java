package com.example.healthmonitoring.meal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String quantity; // Ví dụ: "300g", "2 củ", "1 thìa"

    @Column(nullable = false)
    private Integer displayOrder; // Thứ tự hiển thị
}