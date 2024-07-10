package com.joyboy.productservice.entities.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_attribute")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_option_id")
    private AttributeOption attributeOption;

    @Column(name = "image_url", length = 300)
    @JsonProperty("image_url")
    private String imagUrl;

    private Float price;

    private Float discount_price;

    private LocalDateTime create_at;

    private LocalDateTime update_at;
}
