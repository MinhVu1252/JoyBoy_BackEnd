package com.joyboy.userservice.usecase.token;

import com.joyboy.userservice.entities.models.Token;
import com.joyboy.userservice.entities.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
