package com.joyboy.apigateway.usecase.token;

import com.joyboy.apigateway.entities.dtos.ValidateTokenDTO;
import com.joyboy.apigateway.entities.response.ValidateTokenResponse;
import com.joyboy.apigateway.repositories.ValidateTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final ValidateTokenRepository validateTokenRepository;

    public Mono<ValidateTokenResponse> validToken(String token){
        return validateTokenRepository.validateToken(ValidateTokenDTO.builder()
                .token(token)
                .build());
    }
}
