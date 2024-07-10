package com.joyboy.userservice.repositories;

import com.joyboy.userservice.entities.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT r FROM Role r WHERE r.nameRole = :roleName")
    Role findByName(String roleName);
}
