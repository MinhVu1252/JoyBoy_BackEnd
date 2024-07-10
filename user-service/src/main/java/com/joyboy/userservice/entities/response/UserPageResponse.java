package com.joyboy.userservice.entities.response;

import com.joyboy.userservice.entities.models.User;

import java.util.List;

public record UserPageResponse(List<User> users,
                               Integer pageNumber,
                               Integer pageSize,
                               int totalElements,
                               int totalPages,
                               boolean isLast) {
}
