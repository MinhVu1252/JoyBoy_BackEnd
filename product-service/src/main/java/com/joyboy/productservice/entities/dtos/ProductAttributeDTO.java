package com.joyboy.productservice.entities.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProductAttributeDTO {
    @Min(value = 1, message = "Product's ID must be > 0")
    private Long product_id;

    private List<Long> attribute_option_id;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 100000000, message = "Price must be less than or equal to 100,000,000")
    private Float price;

    @Min(value = 0, message = "Discount price must be greater than or equal to 0")
    @Max(value = 100000000, message = "Price must be less than or equal to 100,000,000")
    private Float discount_price;
}
