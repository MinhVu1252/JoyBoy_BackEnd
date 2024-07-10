package com.joyboy.productservice.entities.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BrandDTO {
    @NotBlank(message = "Brand is required")
    @Size(min = 5, max = 35, message = "Brand name must be between 5 and 35 characters")
    private String brandName;

    private Long categoryId;
}
