package com.Ankit.Score.Score.Payloads;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemDto {
    private Long foodId;
    private String name;
    private int price;
    private String description;
    private CategoryDto category;
}