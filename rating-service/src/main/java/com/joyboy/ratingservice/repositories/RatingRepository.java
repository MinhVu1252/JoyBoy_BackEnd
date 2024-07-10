package com.joyboy.ratingservice.repositories;

import com.joyboy.ratingservice.entities.models.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RatingRepository extends MongoRepository<Rating, String> {
    Page<Rating> findByUserId(Long userId, Pageable pageable);
    Page<Rating> findByProductId(Long productId, Pageable pageable);
}
