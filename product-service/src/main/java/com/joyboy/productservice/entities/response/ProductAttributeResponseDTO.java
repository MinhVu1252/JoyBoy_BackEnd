package com.joyboy.productservice.entities.response;

import com.joyboy.productservice.entities.models.AttributeOption;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProductAttributeResponseDTO {
    private Long productId;
    private List<AttributeOption> attributeOptions;
}
