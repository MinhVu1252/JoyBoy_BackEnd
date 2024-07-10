package com.joyboy.productservice.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.models.Product;
import com.joyboy.productservice.entities.response.ProductPageResponse;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.product.IProductService;
import com.joyboy.productservice.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductPublicController {
    private final IProductService productService;

    //request view image
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpeg").toUri()));
                //return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //request get product by id
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getProductById(
            @PathVariable("id") Long productId
    ) throws DataNotFoundException {
        Product existingProduct = productService.getProductById(productId);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(existingProduct)
                .message("Get product information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request get all products with pagination
    @GetMapping("/getAll")
    public ResponseEntity<ResponseObject> getAllProducts(
            @RequestParam(defaultValue = Constant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = Constant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = Constant.SORT_BY) String productId,
            @RequestParam(defaultValue = Constant.SORT_DIR) String sortDir
    ) {
        ProductPageResponse productPageResponse = productService.getAllProducts(pageNumber, pageSize, productId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(productPageResponse)
                .message("Get list of product information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request get product by category
    @GetMapping("/by-category/{id}")
    public ResponseEntity<ResponseObject> getAllProductsByCategory(
            @PathVariable("id") Long categoryId,
            @RequestParam(defaultValue = Constant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = Constant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = Constant.SORT_BY) String productId,
            @RequestParam(defaultValue = Constant.SORT_DIR) String sortDir
    ) throws DataNotFoundException {
        ProductPageResponse productPageResponse = productService.getAllProductByCategory(categoryId, pageNumber, pageSize, productId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(productPageResponse)
                .message("Get list of product by category information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request search product
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = Constant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = "20", required = false) Integer pageSize,
            @RequestParam(defaultValue = Constant.SORT_BY) String productId,
            @RequestParam(defaultValue = Constant.SORT_DIR) String sortDir
    ) throws Exception {
        ProductPageResponse productPageResponse = productService.searchProduct(keyword, pageNumber, pageSize, productId, sortDir);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get products successfully")
                .status(HttpStatus.OK)
                .data(productPageResponse)
                .build());
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseObject> filterProducts(
            @RequestParam String category,
            @RequestParam String brand,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = Constant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = "20", required = false) Integer pageSize,
            @RequestParam(defaultValue = Constant.SORT_BY) String productId,
            @RequestParam(defaultValue = Constant.SORT_DIR) String sortDir
    ) throws Exception {
        ProductPageResponse productPageResponse = productService.filterProducts(category, brand, minPrice, maxPrice, pageNumber, pageSize, productId, sortDir);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Filter products successfully")
                .status(HttpStatus.OK)
                .data(productPageResponse)
                .build());
    }
}
