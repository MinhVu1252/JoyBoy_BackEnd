package com.joyboy.productservice.controllers.openpublic;

import com.joyboy.productservice.entities.models.Category;
import com.joyboy.productservice.entities.response.CategoryPageResponse;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.category.ICategoryService;
import com.joyboy.productservice.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final ICategoryService categoryService;

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCategoryById(
            @PathVariable("id") Long categoryId
    ) {
        Category existingCategory = categoryService.geCategoryById(categoryId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingCategory)
                .message("Get category information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //get all category with pagination
    @GetMapping("/getAll")
    public ResponseEntity<ResponseObject> getAllCategories(
            @RequestParam(defaultValue = Constant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = Constant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = Constant.SORT_BY) String categoryId,
            @RequestParam(defaultValue = Constant.SORT_DIR) String sortDir
    ) {
        CategoryPageResponse categoryPageResponse = categoryService.getAllCategories(pageNumber, pageSize, categoryId, sortDir);

        return ResponseEntity.ok(ResponseObject.builder()
                .data(categoryPageResponse)
                .message("Get list of category information successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
