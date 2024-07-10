package com.joyboy.productservice.entities.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attribute_type")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attribute_name;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;
}
