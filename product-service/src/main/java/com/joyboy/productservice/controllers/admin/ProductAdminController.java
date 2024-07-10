package com.joyboy.productservice.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.productservice.entities.dtos.ProductAttributeDTO;
import com.joyboy.productservice.entities.dtos.ProductDTO;
import com.joyboy.productservice.entities.models.Product;
import com.joyboy.productservice.entities.models.ProductAttribute;
import com.joyboy.productservice.entities.models.ProductImage;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.product.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final IProductService productService;

    //request add new a product
    @PostMapping("/add-product")
    public ResponseEntity<ResponseObject> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                                        BindingResult result) throws DataNotFoundException {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }
        Product newProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Create new product successfully")
                        .status(HttpStatus.CREATED)
                        .data(newProduct)
                        .build());
    }

    //request add new attribute product
    @PostMapping("/attribute")
    public ResponseEntity<ResponseObject> addProductAttribute(@Valid @RequestBody ProductAttributeDTO productAttributeDTO,
                                                                 BindingResult result) throws DataNotFoundException {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        List<ProductAttribute> newProductAttributes = productService.createProductAttribute(productAttributeDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Create attribute product successfully")
                        .status(HttpStatus.CREATED)
                        .data(newProductAttributes)
                        .build());
    }

    //request upload images for product
    @PostMapping("/uploads/{id}")
    public ResponseEntity<ResponseObject> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ) {
       try {
           List<ProductImage> productImages = productService.uploadImages(productId, files);
           return ResponseEntity.ok().body(ResponseObject.builder()
                   .message("Upload image successfully")
                   .status(HttpStatus.CREATED)
                   .data(productImages)
                   .build());
       } catch (IllegalArgumentException e) {
           return ResponseEntity.badRequest().body(ResponseObject.builder()
                   .message(e.getMessage())
                   .status(HttpStatus.BAD_REQUEST)
                   .build());
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }
    }

    //request upload image for attribute product
    @PostMapping("/uploads/{productId}/attribute-option/{attributeOptionId}")
    public ResponseEntity<ResponseObject> uploadImageProductAttribute(
            @PathVariable("productId") Long productId,
            @PathVariable("attributeOptionId") Long attributeOptionId,
            @RequestParam("file") MultipartFile file
    ) {

        try {
            ProductAttribute productAttribute = productService.uploadImageAttributeProduct(productId, attributeOptionId, file);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload image product attribute successfully")
                    .status(HttpStatus.CREATED)
                    .data(productAttribute)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading image: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    //request update product
    @PatchMapping("/update/{id}")
    public ResponseEntity<ResponseObject> updateProduct(@PathVariable("id") Long productId,
                                                         @Valid @RequestBody ProductDTO productDTO) throws Exception {

        Product updatedProduct = productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(updatedProduct)
                .message("Update product successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request delete product
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable("id") Long productId) throws Exception {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message(String.format("Product with id = %d deleted successfully", productId))
                .status(HttpStatus.OK)
                .build());
    }
}
