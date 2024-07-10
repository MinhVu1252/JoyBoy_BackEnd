package com.joyboy.productservice.repositories;

import com.joyboy.productservice.entities.models.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeTypeRepository extends JpaRepository<AttributeType, Long> {
}
