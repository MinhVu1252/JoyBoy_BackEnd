package com.joyboy.ratingservice.controllers.admin;

import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.ratingservice.entities.responses.RatingPageResponse;
import com.joyboy.ratingservice.uitls.RatingPageConstant;
import com.joyboy.ratingservice.usecase.rating.IRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ratings")
@RequiredArgsConstructor
public class RatingAdminController {
    private final IRatingService ratingService;

    //request get all rating
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRatings(
            @RequestParam(defaultValue = RatingPageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = RatingPageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = RatingPageConstant.SORT_BY) String permissionId,
            @RequestParam(defaultValue = RatingPageConstant.SORT_DIR) String sortDir
    ) {
        RatingPageResponse ratingPageResponse = ratingService.getAllRatings(pageNumber, pageSize, permissionId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ratingPageResponse)
                .message("Get list of rating successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getAllRatingsByUserId(
            @RequestParam(defaultValue = RatingPageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = RatingPageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = RatingPageConstant.SORT_BY) String permissionId,
            @RequestParam(defaultValue = RatingPageConstant.SORT_DIR) String sortDir,
            @PathVariable Long userId
    ) {
        RatingPageResponse ratingPageResponse = ratingService.getAllRatingsByUser(userId, pageNumber, pageSize, permissionId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(ratingPageResponse)
                .message("Get list of rating by user successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
