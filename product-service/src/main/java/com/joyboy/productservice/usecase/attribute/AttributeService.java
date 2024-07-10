package com.joyboy.productservice.usecase.attribute;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.AttributeOptionDTO;
import com.joyboy.productservice.entities.dtos.AttributeTypeDTO;
import com.joyboy.productservice.entities.models.AttributeOption;
import com.joyboy.productservice.entities.models.AttributeType;
import com.joyboy.productservice.entities.models.ProductAttribute;
import com.joyboy.productservice.repositories.AttributeOptionRepository;
import com.joyboy.productservice.repositories.AttributeTypeRepository;
import com.joyboy.productservice.repositories.ProductAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttributeService implements IAttributeService {
    private final AttributeTypeRepository attributeTypeRepository;
    private final AttributeOptionRepository attributeOptionRepository;
    private final ProductAttributeRepository productAttributeRepository;

    @Transactional
    @Override
    public AttributeType createAttributeType(AttributeTypeDTO attributeTypeDTO) {
        AttributeType newAttributeType = AttributeType
                .builder()
                .attribute_name(attributeTypeDTO.getAttribute_type_name())
                .created_at(LocalDateTime.now())
                .build();
        return attributeTypeRepository.save(newAttributeType);
    }

    @Transactional
    @Override
    public AttributeOption createAttributeOption(AttributeOptionDTO attributeOptionDTO) throws DataNotFoundException {
        AttributeType existingAttributeType = attributeTypeRepository.findById(attributeOptionDTO.getAttribute_type_id())
                .orElseThrow(() -> new DataNotFoundException("Attribute type not found with id: " + attributeOptionDTO.getAttribute_type_id()));

        AttributeOption newAttributeOption = AttributeOption.builder()
                .attribute_option_name(attributeOptionDTO.getAttribute_option_name())
                .attribute_option_value(attributeOptionDTO.getAttribute_option_value())
                .attributeType(existingAttributeType)
                .create_at(LocalDateTime.now())
                .build();
        return attributeOptionRepository.save(newAttributeOption);
    }

    @Override
    public AttributeType getAttributeTypeById(Long attributeTypeId) throws DataNotFoundException {
        return attributeTypeRepository.findById(attributeTypeId)
                .orElseThrow(() -> new DataNotFoundException("Attribute type not found with id: " + attributeTypeId));
    }

    @Override
    public AttributeOption getAttributeOptionById(Long attributeOptionId) throws DataNotFoundException {
        return attributeOptionRepository.findById(attributeOptionId)
                .orElseThrow(() -> new DataNotFoundException("AttributeOption not found with id: " + attributeOptionId));
    }

    @Override
    public List<AttributeOption> getAllAttributeOptionsByAttributeType(Long attributeTypeId) throws DataNotFoundException {
        AttributeType attributeType = attributeTypeRepository.findById(attributeTypeId)
                .orElseThrow(() -> new DataNotFoundException("Attribute type not found with id: " + attributeTypeId));

        return attributeOptionRepository.findByAttributeType(attributeType);
    }

    @Override
    public AttributeType updateAttributeType(Long attribute_type_id, AttributeTypeDTO attributeTypeDTO) throws DataNotFoundException {
        return attributeTypeRepository.findById(attribute_type_id)
                .map(existingAttributeType -> {
                    existingAttributeType.setAttribute_name(attributeTypeDTO.getAttribute_type_name());
                    existingAttributeType.setUpdated_at(LocalDateTime.now());
                    return attributeTypeRepository.save(existingAttributeType);
                })
                .orElseThrow(() -> new DataNotFoundException("Attribute type not found with id: " +attribute_type_id));
    }

    @Override
    public AttributeType deleteAttributeType(Long attribute_type_id) throws DataNotFoundException {
        AttributeType attributeType = attributeTypeRepository.findById(attribute_type_id)
                .orElseThrow(() -> new DataNotFoundException("Attribute type not found with id: " + attribute_type_id));

        List<AttributeOption> attributeOptions = attributeOptionRepository.findByAttributeType(attributeType);
        if(!attributeOptions.isEmpty()) {
            throw new IllegalStateException("Cannot delete attribute type with associated attribute options");
        } else {
            attributeTypeRepository.deleteById(attribute_type_id);
            return attributeType;
        }
    }

    @Override
    public AttributeOption updateAttributeOption(Long attribute_option_id, AttributeOptionDTO attributeOptionDTO) throws DataNotFoundException {
        AttributeOption attributeOption = getAttributeOptionById(attribute_option_id);
        AttributeType attributeType = attributeTypeRepository.findById(attributeOptionDTO.getAttribute_type_id())
                .orElseThrow(() -> new DataNotFoundException("Attribute type not found with id: " + attributeOptionDTO.getAttribute_type_id()));

        if(attributeOption == null) {
            throw new DataNotFoundException("AttributeOption not found with id: " + attribute_option_id);
        }

        Optional.ofNullable(attributeOptionDTO.getAttribute_option_name())
                .filter(name -> !name.isEmpty())
                .ifPresent(attributeOption::setAttribute_option_name);

        Optional.ofNullable(attributeOptionDTO.getAttribute_option_value())
                .filter(value -> !value.isEmpty())
                .ifPresent(attributeOption::setAttribute_option_value);

        attributeOption.setAttributeType(attributeType);
        attributeOption.setUpdate_at(LocalDateTime.now());

        return attributeOptionRepository.save(attributeOption);
    }

    @Override
    public AttributeOption deleteAttributeOption(Long attribute_option_id) throws DataNotFoundException {
        AttributeOption attributeOption = attributeOptionRepository.findById(attribute_option_id)
                .orElseThrow(() -> new DataNotFoundException("AttributeOption not found with id: " + attribute_option_id));

        List<ProductAttribute> products = productAttributeRepository.findByAttributeOption(attributeOption);

        if(!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete attribute option with associated products");
        } else {
            attributeOptionRepository.deleteById(attribute_option_id);
            return attributeOption;
        }
    }
}
