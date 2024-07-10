package com.joyboy.productservice.repositories;

import com.joyboy.productservice.entities.models.Brand;
import com.joyboy.productservice.entities.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> getBrandsByCategory(Category category);
}
