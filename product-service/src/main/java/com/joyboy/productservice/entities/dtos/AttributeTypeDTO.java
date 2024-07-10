package com.joyboy.productservice.entities.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTypeDTO {
    @NotBlank(message = "Attribute type is required")
    @Size(min = 2, max = 20, message = "Attribute type must be between 4 and 20 characters")
    private String attribute_type_name;
}
