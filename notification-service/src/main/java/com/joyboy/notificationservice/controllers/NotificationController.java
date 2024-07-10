package com.joyboy.notificationservice.controllers;

import com.joyboy.commonservice.common.request.OtpRequest;
import com.joyboy.notificationservice.usecase.email.IEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final IEmailService emailService;

    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest otpRequest) throws MessagingException {
        // Code to send OTP email to user
        String email = otpRequest.getEmail();
        String otp = otpRequest.getOtp();

        // Use an email service to send the OTP
        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok("OTP sent");
    }
}
