package com.Ankit.Score.Score.Payloads;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FoodItemDto {

    private Long foodId;
    private String name;
    private int price;
    private String description;
    private CategoryDto category;
}