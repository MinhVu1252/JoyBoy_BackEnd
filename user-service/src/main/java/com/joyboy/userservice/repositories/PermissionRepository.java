package com.joyboy.userservice.repositories;

import com.joyboy.userservice.entities.models.Permission;
import com.joyboy.userservice.entities.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNamePermissions(String name);

    List<Permission> getPermissionsByRole(Role role);

    @Query(value = "SELECT COUNT(1) FROM role_permission WHERE permission_id = :permissionId LIMIT 1", nativeQuery = true)
    int countRolePermissionsByPermissionId(@Param("permissionId") Long permissionId);

    @Query(value = "SELECT COUNT(*) FROM role_permission WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    int countRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_permission WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    void deleteFromRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
