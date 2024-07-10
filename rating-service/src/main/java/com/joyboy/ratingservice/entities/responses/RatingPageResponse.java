package com.joyboy.ratingservice.entities.responses;

import com.joyboy.ratingservice.entities.models.Rating;

import java.util.List;

public record RatingPageResponse(List<Rating> ratings,
                                 Integer pageNumber,
                                 Integer pageSize,
                                 int totalElements,
                                 int totalPages,
                                 boolean isLast) {
}
