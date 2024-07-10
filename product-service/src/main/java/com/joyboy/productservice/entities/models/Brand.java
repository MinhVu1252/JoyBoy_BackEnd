package com.joyboy.productservice.entities.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "brands")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brandName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
}
