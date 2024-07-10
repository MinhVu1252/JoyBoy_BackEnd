package com.joyboy.ratingservice.usecase.rating;

import com.joyboy.ratingservice.entities.dtos.RatingDTO;
import com.joyboy.ratingservice.entities.models.Rating;
import com.joyboy.ratingservice.entities.responses.RatingPageResponse;

public interface IRatingService {
    //create rating
    Rating createRating(RatingDTO ratingDTO);

    //get all rating
    RatingPageResponse getAllRatings(Integer pageNumber, Integer pageSize, String sortBy, String dir);

    //get rating by user
    RatingPageResponse getAllRatingsByUser(Long userId, Integer pageNumber, Integer pageSize, String sortBy, String dir);

    //get all rating by product
    RatingPageResponse getAllRatingsByProduct(Long productId, Integer pageNumber, Integer pageSize, String sortBy, String dir);
}
