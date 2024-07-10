package com.joyboy.productservice.entities.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 10, max = 250, message = "Product name must be between 10 and 200 characters")
    private String name;

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 200, message = "Product title must be between 10 and 200 characters")
    private String title;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 100000000, message = "Price must be less than or equal to 100,000,000")
    private Float price;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    @Max(value = 100000000, message = "Price must be less than or equal to 100,000,000")
    private Float discount_price;

    private String description;

    private String thumbnail;

    private Long categoryId;

    private Long brandId;
}
