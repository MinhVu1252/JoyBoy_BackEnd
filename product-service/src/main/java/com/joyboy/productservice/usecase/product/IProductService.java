package com.joyboy.productservice.usecase.product;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.ProductAttributeDTO;
import com.joyboy.productservice.entities.dtos.ProductAttributeImageDTO;
import com.joyboy.productservice.entities.dtos.ProductDTO;
import com.joyboy.productservice.entities.dtos.ProductImageDTO;
import com.joyboy.productservice.entities.models.Product;
import com.joyboy.productservice.entities.models.ProductAttribute;
import com.joyboy.productservice.entities.models.ProductImage;
import com.joyboy.productservice.entities.response.ProductPageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;

    List<ProductAttribute> createProductAttribute(ProductAttributeDTO productAttributeDTO) throws DataNotFoundException;

    Product getProductById(long id) throws DataNotFoundException;

    ProductPageResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String dir);

    ProductPageResponse getAllProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String dir) throws DataNotFoundException;

    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;

    ProductAttribute createImageProductAttribute(ProductAttributeImageDTO productAttributeImageDTO) throws Exception;

    ProductPageResponse searchProduct(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String dir) throws Exception;

    ProductPageResponse filterProducts(String category, String brand, BigDecimal minPrice, BigDecimal maxPrice, Integer pageNumber, Integer pageSize, String sortBy, String dir) throws Exception;

    Product updateProduct(long id, ProductDTO productDTO) throws Exception;

    void deleteProduct(long id) throws Exception;

    List<ProductImage> uploadImages(Long productId, List<MultipartFile> files) throws Exception;

    ProductAttribute uploadImageAttributeProduct(Long productId,  Long attributeOptionId, MultipartFile file) throws Exception;
}
