package com.joyboy.userservice.usecase.role;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.userservice.entities.dtos.PermissionDTO;
import com.joyboy.userservice.entities.dtos.RoleDTO;
import com.joyboy.userservice.entities.models.Role;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    Role createRole(RoleDTO roleDTO) throws Exception;
    List<Role> getAllRoles();
    Role getRoleById(long id) throws DataNotFoundException;
    void deletePermissionInRole(long roleId, long permissionId) throws DataNotFoundException;
    Role addPermissionToRole(Long roleId, Set<PermissionDTO> permissionsDTO) throws Exception;
    Role updateRole(Long roleId, RoleDTO roleDTO) throws DataNotFoundException;
    void deleteRole(Long roleId) throws DataNotFoundException;
}
