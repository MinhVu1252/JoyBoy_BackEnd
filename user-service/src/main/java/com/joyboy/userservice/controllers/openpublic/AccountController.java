package com.joyboy.userservice.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.entities.dtos.UserRegisterDTO;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.entities.request.PasswordResetRequest;
import com.joyboy.userservice.entities.request.PasswordResetVerifyRequest;
import com.joyboy.userservice.usecase.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUsers(@Valid @RequestBody UserRegisterDTO userDTO,
                                                       BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("Account registration successful")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) throws DataNotFoundException {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> verifyOtpAndResetPassword(@RequestBody PasswordResetVerifyRequest request) throws DataNotFoundException {
        boolean success = userService.verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP has expired.");
        }
    }
}
