package com.joyboy.productservice.usecase.category;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.CategoryDTO;
import com.joyboy.productservice.entities.response.CategoryPageResponse;
import com.joyboy.productservice.entities.models.Category;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category geCategoryById(long id);

    CategoryPageResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String dir);

    Category updateCategory(Long categoryId, CategoryDTO categoryDTO) throws DataNotFoundException;

    Category deleteCategory(Long categoryId) throws Exception;
}
