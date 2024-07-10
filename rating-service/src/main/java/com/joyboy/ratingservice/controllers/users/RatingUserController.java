package com.joyboy.ratingservice.controllers.users;

import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.ratingservice.entities.dtos.RatingDTO;
import com.joyboy.ratingservice.entities.models.Rating;
import com.joyboy.ratingservice.usecase.rating.IRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/add-ratings")
@RequiredArgsConstructor
public class RatingUserController {
    private final IRatingService ratingService;

    //request create rating
    @PostMapping("")
    public ResponseEntity<ResponseObject> createRating(@Valid @RequestBody RatingDTO ratingDTO,
                                                       BindingResult result) {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        Rating rating = ratingService.createRating(ratingDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(rating)
                .message("Create rating successful")
                .build());
    }
}
