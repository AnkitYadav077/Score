package com.Ankit.Score.Score.Payloads;

import com.Ankit.Score.Score.Entity.CategoryType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {

    private Long id;
    private String name;
    private CategoryType type;
    private Integer basePrice;
    private Integer eveningPrice;
}