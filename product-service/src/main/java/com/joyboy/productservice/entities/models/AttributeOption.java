package com.joyboy.productservice.entities.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attribute_option")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attribute_option_name;

    private String attribute_option_value;

    @ManyToOne
    @JoinColumn(name = "attribute_type_id")
    private AttributeType attributeType;

    private LocalDateTime create_at;

    private LocalDateTime update_at;

}
