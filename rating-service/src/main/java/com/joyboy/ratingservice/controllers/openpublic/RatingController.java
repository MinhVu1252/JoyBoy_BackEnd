package com.joyboy.ratingservice.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.ratingservice.entities.dtos.RatingDTO;
import com.joyboy.ratingservice.entities.models.Rating;
import com.joyboy.ratingservice.entities.responses.RatingPageResponse;
import com.joyboy.ratingservice.uitls.RatingPageConstant;
import com.joyboy.ratingservice.usecase.rating.IRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.joyboy.commonservice.common.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final IRatingService ratingService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseObject> getAllRatingsByProductId(
            @RequestParam(defaultValue = RatingPageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = RatingPageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = RatingPageConstant.SORT_BY) String permissionId,
            @RequestParam(defaultValue = RatingPageConstant.SORT_DIR) String sortDir,
            @PathVariable Long productId
    ) {
        RatingPageResponse ratingPageResponse = ratingService.getAllRatingsByProduct(productId, pageNumber, pageSize, permissionId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ratingPageResponse)
                .message("Get list of rating by product successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
