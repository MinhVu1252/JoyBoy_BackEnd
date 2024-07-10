package com.joyboy.productservice.entities.response;

import com.joyboy.productservice.entities.models.Category;

import java.util.List;

public record CategoryPageResponse(List<Category> categories,
                                   Integer pageNumber,
                                   Integer pageSize,
                                   int totalElement,
                                   int totalPages,
                                   boolean isLast) {
}
