package com.joyboy.productservice.repositories;

import com.joyboy.productservice.entities.models.AttributeOption;
import com.joyboy.productservice.entities.models.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeOptionRepository extends JpaRepository<AttributeOption, Long> {
    List<AttributeOption> findByAttributeType(AttributeType attributeType);
}
