package com.joyboy.productservice.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.models.Brand;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.brand.IBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandPublicController {
    private final IBrandService brandService;

    //request get brand by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getBrandById(@PathVariable("id") Long brandId) throws DataNotFoundException {
        Brand existingBrand = brandService.getBrandbyId(brandId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingBrand)
                .message("Get brands information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request get brand by category
    @GetMapping("/by-category/{id}")
    public ResponseEntity<ResponseObject> getBrandsByCategory(@PathVariable("id") Long categoryId) throws DataNotFoundException {
        List<Brand> brands = brandService.getAllBrandsByCategory(categoryId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get brands by category information successfully")
                .status(HttpStatus.OK)
                .data(brands)
                .build());
    }
}
