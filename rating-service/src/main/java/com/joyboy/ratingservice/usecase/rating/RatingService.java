package com.joyboy.ratingservice.usecase.rating;

import com.joyboy.ratingservice.entities.dtos.RatingDTO;
import com.joyboy.ratingservice.entities.models.Rating;
import com.joyboy.ratingservice.entities.responses.RatingPageResponse;
import com.joyboy.ratingservice.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final RatingRepository ratingRepository;

    @Override
    public Rating createRating(RatingDTO ratingDTO) {
        Rating rating = Rating.builder()
                .ratingId(UUID.randomUUID().toString())
                .productId(ratingDTO.getProductId())
                .userId(ratingDTO.getUserId())
                .numRating(ratingDTO.getNumRating())
                .feedback(ratingDTO.getFeedback())
                .createAt(LocalDateTime.now())
                .build();
        return ratingRepository.save(rating);
    }

    @Override
    public RatingPageResponse getAllRatings(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Rating> ratingPage = ratingRepository.findAll(pageable);
        return getRatingsPageResponse(pageNumber, pageSize, ratingPage);
    }

    @Override
    public RatingPageResponse getAllRatingsByUser(Long userId,Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Rating> productPage = ratingRepository.findByUserId(userId, pageable);

        return getRatingsPageResponse(pageNumber, pageSize, productPage);
    }

    @Override
    public RatingPageResponse getAllRatingsByProduct(Long productId, Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Rating> productPage = ratingRepository.findByUserId(productId, pageable);

        return getRatingsPageResponse(pageNumber, pageSize, productPage);
    }

    private RatingPageResponse getRatingsPageResponse(Integer pageNumber, Integer pageSize, Page<Rating> userPage) {
        List<Rating> users = userPage.getContent();

        if(users.isEmpty()) {
            return new RatingPageResponse(null, 0, 0, 0, 0, true);
        }

        List<Rating> listUser = new ArrayList<>(users);

        int totalPages = userPage.getTotalPages();
        int totalElements = (int) userPage.getTotalElements();
        boolean isLast = userPage.isLast();

        return new RatingPageResponse(listUser, pageNumber, pageSize, totalElements, totalPages, isLast);
    }
}
