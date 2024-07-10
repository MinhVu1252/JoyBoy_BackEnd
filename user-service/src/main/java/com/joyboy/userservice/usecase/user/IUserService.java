package com.joyboy.userservice.usecase.user;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.InvalidPasswordException;
import com.joyboy.userservice.entities.dtos.ResetPasswordDTO;
import com.joyboy.userservice.entities.dtos.UpdateUserDTO;
import com.joyboy.userservice.entities.dtos.UserRegisterDTO;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.entities.response.UserPageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    User createUser(UserRegisterDTO userDTO) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws DataNotFoundException;

    UserPageResponse findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir);

    void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO)
            throws InvalidPasswordException, DataNotFoundException;

    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;

    void changeProfileImage(Long userId, String imageName) throws Exception;

    void forgotPassword(String email) throws DataNotFoundException;

    boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException;

    void uploadAvatar(Long userId, MultipartFile file) throws Exception;
}
