package com.joyboy.productservice.repositories;

import com.joyboy.productservice.entities.models.Brand;
import com.joyboy.productservice.entities.models.Category;
import com.joyboy.productservice.entities.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);

    List<Product> findByCategory(Category category);

    List<Product> findByBrand(Brand brand);

    @Query(value = "SELECT p.id, p.name_product, p.title, p.price, p.discount_price, p.thumbnail, p.category_id, p.brand_id, p.description, p.created_at, p.updated_at " +
            "FROM products p " +
            "WHERE to_tsvector('simple', p.name_product) @@ plainto_tsquery('simple', :keyword) " +
            "ORDER BY p.id DESC", nativeQuery = true)
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "INNER JOIN p.category c " +
            "INNER JOIN p.brand b " +
            "WHERE (:category IS NULL OR c.name = :category) " +
            "AND (:brand IS NULL OR b.brandName = :brand) " +
            "AND (COALESCE(:minPrice, 0) <= p.discount_price) " +
            "AND (COALESCE(:maxPrice, 100000000) >= p.discount_price) " +
            "ORDER BY p.discount_price DESC")
    Page<Product> filterProducts(@Param("category") String category,
                                 @Param("brand") String brand,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

}