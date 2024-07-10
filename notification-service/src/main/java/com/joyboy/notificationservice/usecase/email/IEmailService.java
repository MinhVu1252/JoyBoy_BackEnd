package com.joyboy.notificationservice.usecase.email;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendOtpEmail(String email, String otp) throws MessagingException;
}
