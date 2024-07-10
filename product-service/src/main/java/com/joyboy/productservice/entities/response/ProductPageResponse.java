package com.joyboy.productservice.entities.response;

import com.joyboy.productservice.entities.models.Product;

import java.util.List;

public record ProductPageResponse(List<Product> products,
                                  Integer pageNumber,
                                  Integer pageSize,
                                  int totalElement,
                                  int totalPages,
                                  boolean isLast) {
}
