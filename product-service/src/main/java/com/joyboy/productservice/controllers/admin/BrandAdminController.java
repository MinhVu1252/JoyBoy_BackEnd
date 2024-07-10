package com.joyboy.productservice.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.productservice.entities.dtos.BrandDTO;

import com.joyboy.productservice.entities.models.Brand;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.brand.IBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/brands")
public class BrandAdminController {

    private final IBrandService brandService;

    //request create brand
    @PostMapping("/add-brand")
    public ResponseEntity<ResponseObject> createBrand(@Valid @RequestBody BrandDTO brandDTO,
                                                      BindingResult bindingResult) throws DataNotFoundException {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Brand newBrand = brandService.createBrand(brandDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Create new brand successfully")
                        .status(HttpStatus.CREATED)
                        .data(newBrand)
                        .build());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ResponseObject> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandDTO brandDTO,
            BindingResult result
    ) throws DataNotFoundException {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        brandService.updateBrand(id, brandDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(brandService.getBrandbyId(id))
                .message("Update brand successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject> deleteBrand(@PathVariable Long id) throws Exception{
        brandService.deleteBrand(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete brand successfully")
                        .build());
    }
}
