package com.joyboy.userservice.usecase.authen;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ExpiredTokenException;
import com.joyboy.commonservice.common.functions.ValidationUtils;
import com.joyboy.userservice.config.jwt.JwtTokenUtils;
import com.joyboy.userservice.entities.dtos.LogoutDTO;
import com.joyboy.userservice.entities.dtos.UserLoginDTO;
import com.joyboy.userservice.entities.dtos.ValidateTokenDTO;
import com.joyboy.userservice.entities.models.Token;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.entities.response.ValidateTokenResponse;
import com.joyboy.userservice.repositories.TokenRepository;
import com.joyboy.userservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenService implements IAuthenticate{
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        String subject = null;

        if (userLoginDTO.getUsername() != null && !userLoginDTO.getUsername().isBlank()) {
            optionalUser = userRepository.findByUsername(userLoginDTO.getUsername());
            subject = userLoginDTO.getUsername();
        }

        // If the user is not found by username, check by email
        if (optionalUser.isEmpty() && userLoginDTO.getEmail() != null) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
            subject = userLoginDTO.getEmail();
        }

        // If user is not found, throw an exception
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("User not found");
        }

        // Get the existing user
        User existingUser = optionalUser.get();

        if(!existingUser.isActive()) {
            throw new DataNotFoundException("User is not active");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginDTO.getPassword(),
                existingUser.getAuthorities()
        );

        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByUsername(subject);
        if (user.isEmpty() && ValidationUtils.isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public ValidateTokenResponse validateToken(ValidateTokenDTO validateTokenDTO) {
        String token = validateTokenDTO.getToken();
        boolean isValid = true;
        List<String> roles = null;

        try {
            // Trích xuất danh sách vai trò từ token
            roles = jwtTokenUtil.extractClaim(token, claims -> {
                List<?> rawRoles = claims.get("roles", List.class);
                return rawRoles.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            });

            // Xác minh token
            jwtTokenUtil.verifyToken(token, false);
        } catch (Exception e) {
            isValid = false;
        }

        return ValidateTokenResponse.builder().valid(isValid).role(roles).build();
    }

    @Override
    public void logout(LogoutDTO logoutDTO) {
        String token = logoutDTO.getToken();

        try {
            // Trích xuất ID (jti) từ token
            String jwtId = jwtTokenUtil.extractClaim(token, Claims::getId);
            // Trích xuất thời gian hết hạn từ token
            Date expiryTime = jwtTokenUtil.extractClaim(token, Claims::getExpiration);

            Token existingToken = tokenRepository.findByJwtId(jwtId);
            if (existingToken != null) {
                // Nếu tồn tại, cập nhật token
                existingToken.setExpired(true);
                existingToken.setRevoked(true);
                existingToken.setRefreshExpirationDate(LocalDateTime.now());
                existingToken.setExpirationDate(expiryTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                tokenRepository.save(existingToken);
            }else {
//                log.info("Token already invalidated");
            }
        } catch (ExpiredJwtException e) {
//            log.info("Token already expired");
        }
    }
}
