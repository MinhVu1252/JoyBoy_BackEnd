package com.joyboy.productservice.usecase.category;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.CategoryDTO;
import com.joyboy.productservice.entities.models.Brand;
import com.joyboy.productservice.entities.models.Product;
import com.joyboy.productservice.entities.response.CategoryPageResponse;
import com.joyboy.productservice.entities.models.Category;
import com.joyboy.productservice.repositories.BrandRepository;
import com.joyboy.productservice.repositories.CategoryRepository;
import com.joyboy.productservice.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Transactional
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category
                .builder()
                .name(categoryDTO.getName())
                .createdAt(LocalDateTime.now())
                .build();
        return this.categoryRepository.save(newCategory);
    }

    @Override
    public Category geCategoryById(long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public CategoryPageResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            return new CategoryPageResponse(null, 0, 0, 0, 0, true);
        }
        List<Category> categoryList = new ArrayList<>(categories);

        int totalElement = (int) categoryPage.getTotalElements();
        int totalPages = categoryPage.getTotalPages();
        boolean isLast = categoryPage.isLast();

        return new CategoryPageResponse(categoryList, pageNumber, pageSize, totalElement, totalPages, isLast);
    }

    @Transactional
    @Override
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) throws DataNotFoundException {
        return categoryRepository.findById(categoryId)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDTO.getName());
                    existingCategory.setUpdatedAt(LocalDateTime.now());
                    return categoryRepository.save(existingCategory);
                })
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + categoryId));
    }

    @Transactional
    @Override
    public Category deleteCategory(Long categoryId) throws Exception {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + categoryId));

        List<Brand> brands = brandRepository.getBrandsByCategory(category);

        List<Product> products = productRepository.findByCategory(category);
        if (!products.isEmpty() || !brands.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products or brands");
        } else {
            categoryRepository.deleteById(categoryId);
            return category;
        }
    }
}
