package com.joyboy.productservice.usecase.attribute;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.dtos.AttributeOptionDTO;
import com.joyboy.productservice.entities.dtos.AttributeTypeDTO;
import com.joyboy.productservice.entities.models.AttributeOption;
import com.joyboy.productservice.entities.models.AttributeType;

import java.util.List;

public interface IAttributeService {
    //create attribute type
    AttributeType createAttributeType(AttributeTypeDTO attributeTypeDTO);

    //create attribute option
    AttributeOption createAttributeOption(AttributeOptionDTO attributeOptionDTO) throws DataNotFoundException;

    //get attribute type by id
    AttributeType getAttributeTypeById(Long attributeTypeId) throws DataNotFoundException;

    //get attribute option by id
    AttributeOption getAttributeOptionById(Long attributeOptionId) throws DataNotFoundException;

    //get attribute option by attribute type
    List<AttributeOption> getAllAttributeOptionsByAttributeType(Long attributeTypeId) throws DataNotFoundException;

    //update attribute type
    AttributeType updateAttributeType(Long attribute_type_id, AttributeTypeDTO attributeTypeDTO) throws DataNotFoundException;

    //delete attribute type
    AttributeType deleteAttributeType(Long attribute_type_id) throws DataNotFoundException;

    //update attribute option
    AttributeOption updateAttributeOption(Long attribute_option_id, AttributeOptionDTO attributeOptionDTO) throws DataNotFoundException;

    //delete attribute option
    AttributeOption deleteAttributeOption(Long attribute_option_id) throws DataNotFoundException;
}
