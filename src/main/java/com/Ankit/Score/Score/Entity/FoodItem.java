package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    private String name;
    private int price;
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}