package com.joyboy.userservice.usecase.user;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ExistsException;
import com.joyboy.commonservice.common.exceptions.InvalidPasswordException;
import com.joyboy.commonservice.common.request.OtpRequest;
import com.joyboy.userservice.entities.dtos.ResetPasswordDTO;
import com.joyboy.userservice.entities.dtos.UpdateUserDTO;
import com.joyboy.userservice.entities.dtos.UserRegisterDTO;
import com.joyboy.userservice.entities.models.Role;
import com.joyboy.userservice.entities.models.Token;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.entities.response.UserPageResponse;
import com.joyboy.userservice.repositories.RoleRepository;
import com.joyboy.userservice.repositories.TokenRepository;
import com.joyboy.userservice.repositories.UserRepository;
import com.joyboy.userservice.utils.RoleConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    @Override
    public User createUser(UserRegisterDTO userDTO) throws ExistsException {
        if(!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ExistsException("Email already exists");
        }

        if(!userDTO.getUsername().isBlank() && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ExistsException("Username already exists");
        }

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(RoleConstant.USER_ROLE);
        roles.add(role);

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(roles)
                .active(true)
                .create_at(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Optional.ofNullable(updateUserDTO.getUsername())
                .filter(name -> !name.isEmpty())
                .ifPresent( existingUser::setUsername);

        Optional.ofNullable(updateUserDTO.getFirstName())
                .filter(firstName -> !firstName.isEmpty())
                .ifPresent( existingUser::setFirstName);

        Optional.ofNullable(updateUserDTO.getLastName())
                .filter(lastName -> !lastName.isEmpty())
                .ifPresent(existingUser::setLastName);

        Optional.ofNullable(updateUserDTO.getEmail())
                .filter(email -> !email.isEmpty())
                .ifPresent(existingUser::setEmail);

        Optional.ofNullable(updateUserDTO.getPhoneNumber())
                .filter(phoneNumber -> !phoneNumber.isEmpty())
                .ifPresent(existingUser::setPhoneNumber);

        return userRepository.save(existingUser);
    }

    @Override
    public UserPageResponse findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        return getUsersPageResponse(pageNumber, pageSize, userPage);
    }

    private UserPageResponse getUsersPageResponse(Integer pageNumber, Integer pageSize, Page<User> userPage) {
        List<User> users = userPage.getContent();

        if(users.isEmpty()) {
            return new UserPageResponse(null, 0, 0, 0, 0, true);
        }

        List<User> listUser = new ArrayList<>(users);

        int totalPages = userPage.getTotalPages();
        int totalElements = (int) userPage.getTotalElements();
        boolean isLast = userPage.isLast();

        return new UserPageResponse(listUser, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

    @Override
    public void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO) throws InvalidPasswordException, DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (!passwordEncoder.matches(resetPasswordDTO.getOldPassword(), existingUser.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);

        List<Token> tokens = tokenRepository.findByUser(existingUser);
        tokenRepository.deleteAll(tokens);
    }

    @Override
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    public void changeProfileImage(Long userId, String imageName) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }

    @Override
    public void forgotPassword(String email) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

        user.setOtp(passwordEncoder.encode(otp));
        user.setExpiryOtp(expiryTime);
        userRepository.save(user);

        sendOtpToNotificationService(user.getEmail(), otp);
    }

    @Override
    public boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (passwordEncoder.matches(otp, user.getOtp()) && user.getExpiryOtp().isAfter(LocalDateTime.now())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setOtp(null);  // Clear OTP
            user.setExpiryOtp(null);  // Clear OTP expiry time
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void uploadAvatar(Long userId, MultipartFile file) throws Exception {
            User existingUser = userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new DataNotFoundException("User Not Found"));

        validateFileUpload(file);

        WebClient webClient = webClientBuilder.build();

        String imageAvatar = webClient.post()
                .uri("http://localhost:5002/api/v1/internal/files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(String.class)
                .block();


        changeProfileImage(existingUser.getId(), imageAvatar);
    }

    private String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        StringBuilder output = new StringBuilder(Integer.toString(randomNumber));

        while (output.length() < 6) {
            output.insert(0, "0");
        }
        return output.toString();
    }

    private void sendOtpToNotificationService(String email, String otp) {
        WebClient webClient = webClientBuilder.build();
        String notificationServiceUrl = "http://localhost:5009/api/v1/notification/sendOtp";

        OtpRequest otpRequest = new OtpRequest(email, otp);
        webClient.post()
                .uri(notificationServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(otpRequest))
                .retrieve()
                .bodyToMono(String.class)
                .block();  // Blocking for immediate response
    }

    private void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large! Maximum size is 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }
}
