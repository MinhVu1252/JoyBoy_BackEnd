package com.joyboy.productservice.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.productservice.entities.dtos.AttributeOptionDTO;
import com.joyboy.productservice.entities.dtos.AttributeTypeDTO;
import com.joyboy.productservice.entities.models.AttributeOption;
import com.joyboy.productservice.entities.models.AttributeType;
import com.joyboy.productservice.entities.response.ResponseObject;
import com.joyboy.productservice.usecase.attribute.IAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/attributes")
@RequiredArgsConstructor
public class AttributeAdminController {
    private final IAttributeService attributeService;

    //request create attribute type
    @PostMapping("/add-type")
    public ResponseEntity<ResponseObject> addAttribute(@Valid @RequestBody AttributeTypeDTO attributeTypeDTO,
                                                       BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        AttributeType attributeType = attributeService.createAttributeType(attributeTypeDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create attribute type successfully")
                .status(HttpStatus.OK)
                .data(attributeType)
                .build());
    }

    //request create attribute option
    @PostMapping("/add-option")
    public ResponseEntity<ResponseObject> addAttributeOption(@Valid @RequestBody AttributeOptionDTO attributeOptionDTO,
                                                             BindingResult bindingResult) throws DataNotFoundException {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        AttributeOption attributeOption = attributeService.createAttributeOption(attributeOptionDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Create attribute option successfully")
                .status(HttpStatus.OK)
                .data(attributeOption)
                .build());
    }

    //request update attribute type
    @PatchMapping("/update/type/{id}")
    public ResponseEntity<ResponseObject> updateAttributeType(@Valid @PathVariable Long id,
                                                              @RequestBody AttributeTypeDTO attributeTypeDTO) throws DataNotFoundException {
        attributeService.updateAttributeType(id, attributeTypeDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(attributeService.getAttributeTypeById(id))
                .message("Update attribute type successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request delete attribute type
    @DeleteMapping("/delete/type/{id}")
    public ResponseEntity<ResponseObject> deleteAttributeType(@PathVariable Long id) throws DataNotFoundException {
        attributeService.deleteAttributeType(id);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .message("delete attribute type successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request update attribute option
    @PatchMapping("/update/option/{id}")
    public ResponseEntity<ResponseObject> updateAttributeOption(@Valid @PathVariable Long id,
                                                              @RequestBody AttributeOptionDTO attributeOptionDTO) throws DataNotFoundException {
        attributeService.updateAttributeOption(id, attributeOptionDTO);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .data(attributeService.getAttributeOptionById(id))
                .message("Update attribute option successfully")
                .status(HttpStatus.OK)
                .build());
    }

    //request delete attribute option
    @DeleteMapping("/delete/option/{id}")
    public ResponseEntity<ResponseObject> deleteAttributeOption(@PathVariable Long id) throws DataNotFoundException {
        attributeService.deleteAttributeOption(id);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .message("delete attribute option successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
