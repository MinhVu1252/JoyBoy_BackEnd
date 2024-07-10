package com.joyboy.ratingservice.entities.dtos;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private Long userId;

    private Long productId;

    @Max(value = 5, message = "Number rating must be 0 to 5")
    private int numRating;

    private String feedback;
}
