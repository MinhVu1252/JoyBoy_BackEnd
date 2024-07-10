package com.joyboy.productservice.usecase.product;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.InvalidParamException;
import com.joyboy.productservice.entities.dtos.ProductAttributeDTO;
import com.joyboy.productservice.entities.dtos.ProductAttributeImageDTO;
import com.joyboy.productservice.entities.dtos.ProductDTO;
import com.joyboy.productservice.entities.dtos.ProductImageDTO;
import com.joyboy.productservice.entities.models.*;
import com.joyboy.productservice.entities.response.ProductPageResponse;
import com.joyboy.productservice.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final ProductImageRepository productImageRepository;
    private final BrandRepository brandRepository;
    private final WebClient.Builder webClientBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    //add new product
    @Transactional
    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find category with id: "+productDTO.getCategoryId()));

        Brand existingBrand = brandRepository
                .findById(productDTO.getBrandId())
                .orElseThrow(() -> new DataNotFoundException(("Cannot find brand with id: "+productDTO.getBrandId())));

        Product newProduct = Product.builder()
                .name_product(productDTO.getName())
                .title(productDTO.getTitle())
                .price(productDTO.getPrice())
                .discount_price(productDTO.getDiscount_price())
                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .created_at(LocalDateTime.now())
                .category(existingCategory)
                .brand(existingBrand)
                .build();
        return productRepository.save(newProduct);
    }

    //add new attribute for a product
    @Transactional
    @Override
    public List<ProductAttribute> createProductAttribute(ProductAttributeDTO productAttributeDTO) throws DataNotFoundException {
        Product product = productRepository.findById(productAttributeDTO.getProduct_id())
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: "+productAttributeDTO.getProduct_id()));

        List<AttributeOption> attributeOptions = new ArrayList<>();
        for(Long attribute_option_id : productAttributeDTO.getAttribute_option_id()) {
            AttributeOption attributeOption = attributeOptionRepository.findById(attribute_option_id)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find attribute option with id: "+attribute_option_id));
            attributeOptions.add(attributeOption);
        }

        List<ProductAttribute> productAttributes = new ArrayList<>();
        for (AttributeOption attributeOption : attributeOptions) {
            ProductAttribute productAttribute = ProductAttribute.builder()
                    .product(product)
                    .attributeOption(attributeOption)
                    .price(productAttributeDTO.getPrice())
                    .discount_price(productAttributeDTO.getDiscount_price())
                    .create_at(LocalDateTime.now())
                    .build();
            productAttributes.add(productAttributeRepository.save(productAttribute));
        }
        return productAttributes;
    }

    //get product by id
    @Override
    public Product getProductById(long id) throws DataNotFoundException {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
    }

    //get all products with pagination
    @Override
    public ProductPageResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findAll(pageable);

        return getProductPageResponse(pageNumber, pageSize, productPage);
    }

    private ProductPageResponse getProductPageResponse(Integer pageNumber, Integer pageSize, Page<Product> productPage) {
        List<Product> products = productPage.getContent();

        if(products.isEmpty()) {
            return new ProductPageResponse(null, 0, 0, 0, 0, true);
        }

        List<Product> productList = new ArrayList<>(products);

        int totalPages = productPage.getTotalPages();
        int totalElements = (int) productPage.getTotalElements();
        boolean isLast = productPage.isLast();

        return new ProductPageResponse(productList, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

    //get product by category with pagination
    @Override
    public ProductPageResponse getAllProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String dir) throws DataNotFoundException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findByCategory(category, pageable);

        return getProductPageResponse(pageNumber, pageSize, productPage);
    }

    //add product image
    @Transactional
    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find product with id: "+productImageDTO.getProductId()));

        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                    "Number of images must be <= "
                            +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        if (existingProduct.getThumbnail() == null ) {
            existingProduct.setThumbnail(newProductImage.getImageUrl());
        }
        productRepository.save(existingProduct);
        return productImageRepository.save(newProductImage);
    }

    //add image for attribute product
    @Transactional
    @Override
    public ProductAttribute createImageProductAttribute(ProductAttributeImageDTO productAttributeImageDTO) throws Exception {
        ProductAttribute existingAttribute = productAttributeRepository.findByProductIdAndAttributeOptionId(
                productAttributeImageDTO.getProductId(),
                productAttributeImageDTO.getAttributeOptionId()
        );

        if (existingAttribute != null) {
            existingAttribute.setImagUrl(productAttributeImageDTO.getImageUrl());
            return productAttributeRepository.save(existingAttribute);
        } else {
            throw new DataNotFoundException("Product attribute not found");
        }
    }

    @Override
    public ProductPageResponse searchProduct(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
        return getProductPageResponse(pageNumber, pageSize, productPage);
    }

    @Override
    public ProductPageResponse filterProducts(String category, String brand, BigDecimal minPrice, BigDecimal maxPrice, Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.filterProducts(category,brand,minPrice, maxPrice, pageable);
        return getProductPageResponse(pageNumber, pageSize, productPage);
    }


    @Transactional
    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Objects.requireNonNull(productDTO, "Product must not be null");

        Product existingProduct = getProductById(id);

        if (existingProduct == null) {
            throw new DataNotFoundException("Product not found with id: " + id);
        }

        Optional.ofNullable(productDTO.getName())
                .filter(name -> !name.isEmpty())
                .ifPresent(existingProduct::setName_product);

        Optional.ofNullable(productDTO.getTitle())
                .filter(title -> !title.isEmpty())
                .ifPresent(existingProduct::setTitle);

        Optional.ofNullable(productDTO.getPrice())
                .filter(price -> price >= 0)
                .ifPresent(existingProduct::setPrice);

        Optional.ofNullable(productDTO.getDiscount_price())
                .filter(discountPrice -> discountPrice >= 0)
                .ifPresent(existingProduct::setDiscount_price);

        Optional.ofNullable(productDTO.getDescription())
                .filter(description -> !description.isEmpty())
                .ifPresent(existingProduct::setDescription);

        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
        existingProduct.setCategory(existingCategory);

        Brand existingBrand = brandRepository.findById(productDTO.getBrandId())
                        .orElseThrow(() -> new DataNotFoundException("Brand not found with id: " + productDTO.getBrandId()));

        existingProduct.setUpdated_at(LocalDateTime.now());

        existingProduct.setBrand(existingBrand);

        return productRepository.save(existingProduct);
    }

    @Transactional
    @Override
    public void deleteProduct(long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    @Transactional
    @Override
    public List<ProductImage> uploadImages(Long productId, List<MultipartFile> files) throws Exception {
        Product existingProduct = getProductById(productId);
        files = files == null ? new ArrayList<>() : files;

        if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new IllegalArgumentException("You can only upload a maximum of 5 images");
        }

        List<ProductImage> productImages = new ArrayList<>();
        WebClient webClient = webClientBuilder.build();

        for (MultipartFile file : files) {
            if (file.getSize() == 0) {
                continue;
            }

            validateFileUpload(file);

            String filename = webClient.post()
                    .uri("http://localhost:5002/api/v1/internal/files/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", file.getResource()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ProductImage productImage = createProductImage(
                    existingProduct.getId(),
                    ProductImageDTO.builder()
                            .imageUrl(filename)
                            .build()
            );
            productImages.add(productImage);
        }

        return productImages;
    }

    @Transactional
    @Override
    public ProductAttribute uploadImageAttributeProduct(Long productId, Long attributeOptionId, MultipartFile file) throws Exception {
        validateFileUpload(file);

        WebClient webClient = webClientBuilder.build();

        // Lưu file và lấy URL của hình ảnh
        String imageUrl = webClient.post()
                .uri("http://localhost:5002/api/v1/internal/files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Tạo DTO để lưu thông tin hình ảnh vào sản phẩm
        ProductAttributeImageDTO attributeImageDTO = ProductAttributeImageDTO.builder()
                .productId(productId)
                .attributeOptionId(attributeOptionId)
                .imageUrl(imageUrl)
                .build();

        // Gọi ProductService để lưu hình ảnh vào sản phẩm và trả về sản phẩm đã được cập nhật
        return createImageProductAttribute(attributeImageDTO);
    }

    private void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large! Maximum size is 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }
}
