package com.joyboy.productservice.usecase.brand;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.BrandDTO;
import com.joyboy.productservice.entities.models.Brand;
import com.joyboy.productservice.entities.models.Category;
import com.joyboy.productservice.entities.models.Product;
import com.joyboy.productservice.repositories.BrandRepository;
import com.joyboy.productservice.repositories.CategoryRepository;
import com.joyboy.productservice.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BrandService implements IBrandService {
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public Brand createBrand(BrandDTO brandDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(brandDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        Brand brand = Brand.builder()
                .brandName(brandDTO.getBrandName())
                .category(existingCategory)
                .created_at(LocalDateTime.now())
                .build();

        return brandRepository.save(brand);
    }

    @Override
    public Brand getBrandbyId(Long id) throws DataNotFoundException {
        return brandRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Brand not found"));
    }

    @Override
    public List<Brand> getAllBrandsByCategory(Long categoryId) throws DataNotFoundException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        return brandRepository.getBrandsByCategory(category);
    }

    @Override
    public Brand updateBrand(Long brandId, BrandDTO brandDTO) throws DataNotFoundException {
        Brand existingBrand = getBrandbyId(brandId);
        Category existingCategory = categoryRepository.findById(brandDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + brandDTO.getCategoryId()));

        if (existingBrand == null) {
            throw new DataNotFoundException("Brand not found with id " + brandId);
        }

        Optional.ofNullable(brandDTO.getBrandName())
                .filter(name -> !name.isEmpty())
                .ifPresent(existingBrand::setBrandName);

        existingBrand.setCategory(existingCategory);
        existingBrand.setUpdated_at(LocalDateTime.now());

        return brandRepository.save(existingBrand);
    }

    @Override
    public Brand deleteBrand(Long brandId) throws DataNotFoundException {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new DataNotFoundException("Brand not found with id: " + brandId));

        List<Product> products = productRepository.findByBrand(brand);

        if (!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete brand with associated products");
        } else {
            brandRepository.deleteById(brandId);
            return brand;
        }
    }
}
