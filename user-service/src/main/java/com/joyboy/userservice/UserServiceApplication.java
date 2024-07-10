package com.joyboy.userservice;

import com.joyboy.userservice.entities.models.Role;
import com.joyboy.userservice.repositories.RoleRepository;
import com.joyboy.userservice.utils.RoleConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient
@RequiredArgsConstructor
public class UserServiceApplication implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        addRoleExisting(RoleConstant.ADMIN_ROLE);
        addRoleExisting(RoleConstant.USER_ROLE);
    }

    void addRoleExisting(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if(role == null) {
            role = Role.builder()
                    .nameRole(roleName)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        roleRepository.save(role);
    }
}
