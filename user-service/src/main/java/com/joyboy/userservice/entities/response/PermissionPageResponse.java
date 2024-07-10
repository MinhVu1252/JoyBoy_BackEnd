package com.joyboy.userservice.entities.response;

import com.joyboy.userservice.entities.models.Permission;

import java.util.List;

public record PermissionPageResponse(List<Permission> permissions,
                                     Integer pageNumber,
                                     Integer pageSize,
                                     int totalElements,
                                     int totalPages,
                                     boolean isLast)  {
}
