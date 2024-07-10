package com.joyboy.userservice.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.entities.dtos.PermissionDTO;
import com.joyboy.userservice.entities.dtos.RoleDTO;
import com.joyboy.userservice.entities.models.Role;
import com.joyboy.userservice.usecase.role.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/roles")
public class RoleController {
    private final IRoleService roleService;

    @PostMapping("/add-role")
    public ResponseEntity<ResponseObject> createRole(@Valid @RequestBody RoleDTO roleDTO,
                                                     BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Role role = roleService.createRole(roleDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(role)
                .message("Create Role successful")
                .build());
    }

    @PostMapping("/{roleId}/add-permission")
    public ResponseEntity<ResponseObject> addPermissionToRole(@Valid
                                                              @PathVariable Long roleId,
                                                              @RequestBody Set<PermissionDTO> permissionDTO,
                                                              BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Role role = roleService.addPermissionToRole(roleId, permissionDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(role)
                .message("Add permission to role successful")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteRole(@PathVariable Long id) throws Exception {
        roleService.deleteRole(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete role successfully")
                        .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject> updateRole(@Valid @PathVariable Long id,
                                                     @RequestBody RoleDTO roleDTO) throws Exception {
        Role updateRole = roleService.updateRole(id, roleDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Update role successfully")
                        .data(updateRole)
                        .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> role = roleService.getAllRoles();
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get list role successfully")
                        .data(role)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getRoleById(@PathVariable Long id) throws DataNotFoundException {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get information role successfully")
                        .data(role)
                        .build());
    }

    @DeleteMapping("/{roleId}/permission/{permissionId}")
    public ResponseEntity<ResponseObject> deletePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) throws Exception {
        roleService.deletePermissionInRole(roleId, permissionId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete permission from successfully")
                        .build());
    }
}
