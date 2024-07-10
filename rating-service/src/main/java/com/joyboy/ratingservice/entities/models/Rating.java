package com.joyboy.ratingservice.entities.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("ratings")
public class Rating {
    @Id
    private String ratingId;

    private Long userId;

    private Long productId;

    private int numRating;

    private String feedback;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
