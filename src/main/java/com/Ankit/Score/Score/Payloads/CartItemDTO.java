package com.Ankit.Score.Score.Payloads;

import com.Ankit.Score.Score.Entity.FoodItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long foodId;
    private String foodName;
    private int quantity;
    private double price;
    private String categoryName;

    // Optionally, you can have the full FoodItem object if you want to send it
    @JsonIgnore
    private FoodItem foodItem;   // Marked @JsonIgnore to avoid infinite recursion if serialized
}
