package com.joyboy.productservice.usecase.brand;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.BrandDTO;
import com.joyboy.productservice.entities.models.Brand;

import java.util.List;

public interface IBrandService {
    Brand createBrand(BrandDTO brandDTO) throws DataNotFoundException;

    Brand getBrandbyId(Long id) throws DataNotFoundException;

    List<Brand> getAllBrandsByCategory(Long categoryId) throws DataNotFoundException;

    Brand updateBrand(Long brandId, BrandDTO brandDTO) throws DataNotFoundException;

    Brand deleteBrand(Long brandId) throws DataNotFoundException;
}
