package com.joyboy.userservice.controllers.user;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.InvalidPasswordException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.entities.dtos.ResetPasswordDTO;
import com.joyboy.userservice.entities.dtos.UpdateUserDTO;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.usecase.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PatchMapping("/update/{userId}")
    public ResponseEntity<ResponseObject> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO,
                                                     @PathVariable Long userId,
                                                     BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User updateUser = userService.updateUser(userId, updateUserDTO);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(updateUser)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/change-password/{userId}")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @RequestBody ResetPasswordDTO request,
                                                        BindingResult result,
                                                        @PathVariable Long userId) {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        try {
            userService.resetPassword(userId, request);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(null)
                    .message("Reset password successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Invalid old password")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("User not found")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("An error occurred")
                    .data("")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PostMapping("/uploads/{userId}")
    public ResponseEntity<ResponseObject> uploadAvatarUser(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file
    ) {

        try {
            userService.uploadAvatar(userId, file);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload image avatar successfully")
                    .status(HttpStatus.CREATED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading image: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
}
