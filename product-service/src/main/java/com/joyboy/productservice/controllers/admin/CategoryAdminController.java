package com.joyboy.productservice.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.productservice.entities.dtos.CategoryDTO;
import com.joyboy.productservice.entities.response.CategoryPageResponse;
import com.joyboy.productservice.entities.models.Category;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.category.ICategoryService;
import com.joyboy.productservice.utils.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final ICategoryService categoryService;

    //create category
    @PostMapping("/add-category")
    public ResponseEntity<ResponseObject> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }
        Category category = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create category successfully")
                .status(HttpStatus.OK)
                .data(category)
                .build());
    }

    //update category
    @PatchMapping("/update/{id}")
    public ResponseEntity<ResponseObject> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) throws DataNotFoundException {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(categoryService.geCategoryById(id))
                .message("Update category successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //delete category
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) throws Exception{
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete category successfully")
                        .build());
    }
}
