package com.joyboy.productservice.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.productservice.entities.models.AttributeOption;
import com.joyboy.productservice.entities.models.AttributeType;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.attribute.IAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributePublicController {
    private final IAttributeService attributeService;

    //request get attribute type by id
    @GetMapping("/attribute-type/{id}")
    public ResponseEntity<ResponseObject> getAttributeTypeById(@PathVariable("id") Long attributeTypeId) throws DataNotFoundException {
        AttributeType attributeType = attributeService.getAttributeTypeById(attributeTypeId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get attribute type information successfully")
                .status(HttpStatus.OK)
                .data(attributeType)
                .build());
    }

    //request get attribute option by id
    @GetMapping("/attribute-option/{id}")
    public ResponseEntity<ResponseObject> getAttributeOptionById(@PathVariable("id") Long attributeOptionId) throws DataNotFoundException {
        AttributeOption attributeOption = attributeService.getAttributeOptionById(attributeOptionId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get attribute option information successfully")
                .status(HttpStatus.OK)
                .data(attributeOption)
                .build());
    }

    //request get attribute option by attribute type
    @GetMapping("/attribute-type/{id}/options")
    public ResponseEntity<ResponseObject> getAttributeOptionByAttributeType(@PathVariable("id") Long attributeTypeId) throws DataNotFoundException {
        List<AttributeOption> attributeOptions = attributeService.getAllAttributeOptionsByAttributeType(attributeTypeId);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get attribute option by attribute type information successfully")
                .status(HttpStatus.OK)
                .data(attributeOptions)
                .build());
    }
}
