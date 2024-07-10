package com.joyboy.productservice.repositories;

import com.joyboy.productservice.entities.models.AttributeOption;
import com.joyboy.productservice.entities.models.Product;
import com.joyboy.productservice.entities.models.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    ProductAttribute findByProductIdAndAttributeOptionId(Long productId, Long attributeOptionId);
    List<ProductAttribute> findByAttributeOption(AttributeOption attributeOption);
}
