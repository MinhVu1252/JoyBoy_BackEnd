package com.joyboy.userservice.usecase.permission;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.userservice.entities.dtos.PermissionDTO;
import com.joyboy.userservice.entities.models.Permission;
import com.joyboy.userservice.entities.response.PermissionPageResponse;

import java.util.List;

public interface IPermission {
    Permission createPermission(PermissionDTO permissionDTO) throws Exception;
    PermissionPageResponse getAllPermission(Integer pageNumber, Integer pageSize, String sortBy, String dir);
    Permission getPermissionById(long id) throws DataNotFoundException;
    Permission updatePermission(Long permissionId, PermissionDTO permissionDTO) throws DataNotFoundException;
    void deletePermission(Long permissionId) throws DataNotFoundException;
}
