package com.joyboy.productservice.entities.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeOptionDTO {
    @Size(min = 0, max = 30, message = "Attribute option name must be between 0 and 30 characters")
    private String attribute_option_name;

    @Size(min = 0, max = 30, message = "Attribute option value must be between 0 and 30 characters")
    private String attribute_option_value;

    private Long attribute_type_id;
}
