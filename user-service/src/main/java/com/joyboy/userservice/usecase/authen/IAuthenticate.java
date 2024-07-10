package com.joyboy.userservice.usecase.authen;

import com.joyboy.userservice.entities.dtos.LogoutDTO;
import com.joyboy.userservice.entities.dtos.UserLoginDTO;
import com.joyboy.userservice.entities.dtos.ValidateTokenDTO;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.entities.response.ValidateTokenResponse;

public interface IAuthenticate {
    String login(UserLoginDTO userLoginDTO) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    ValidateTokenResponse validateToken(ValidateTokenDTO validateTokenDTO) throws Exception;

    void logout(LogoutDTO logoutDTO) throws Exception;
}
