package com.joyboy.apigateway.repositories;

import com.joyboy.apigateway.entities.dtos.ValidateTokenDTO;
import com.joyboy.apigateway.entities.response.ValidateTokenResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface ValidateTokenRepository {
    @PostExchange(url = "", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ValidateTokenResponse> validateToken(@RequestBody ValidateTokenDTO request);
}
