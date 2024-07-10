package com.joyboy.apigateway.config;

import com.joyboy.apigateway.entities.response.ApiResponse;
import com.joyboy.apigateway.entities.response.ValidateTokenResponse;
import com.joyboy.apigateway.usecase.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final TokenService tokenService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final String apiPrefix = "/api/v1";

    private final List<String> publicEndpoints = Arrays.asList(
            "/brands/**",
            "/categories/**",
            "/products/**",
            "/attributes/**",
            "/auth/**",
            "/account/**",
            "/ratings/**"
    );

    private final List<String> adminEndpoints = Arrays.asList(
            "/admin/brands/**",
            "/admin/categories/**",
            "/admin/products/**",
            "/admin/attributes/**",
            "/admin/users/**",
            "/admin/permissions/**",
            "/admin/roles/**",
            "/admin/ratings/**"
    );

    private final List<String> userEndpoints = Arrays.asList(
            "/users/**",
            "/add-ratings/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicEndpoint(request)) {
            return chain.filter(exchange);
        }

        Optional<String> tokenOptional = extractToken(request);
        return tokenOptional.map(token ->
                tokenService.validToken(token)
                        .flatMap(tokenResponse -> {
                            if (!tokenResponse.isValid()) {
                                return unauthenticated(exchange.getResponse());
                            }
                            if (isAdminEndpoint(request) && !isAdmin(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            if (isUserEndpoint(request) && !isUser(tokenResponse)) {
                                return unauthorized(exchange.getResponse());
                            }
                            return chain.filter(exchange);
                        })
                        .onErrorResume(e -> handleError(exchange.getResponse()))
        ).orElseGet(() -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return publicEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isAdminEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return adminEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isUserEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return userEndpoints.stream().anyMatch(endpoint -> pathMatcher.match(apiPrefix + endpoint, path));
    }

    private boolean isAdmin(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("ADMIN");
    }

    private boolean isUser(ValidateTokenResponse tokenResponse) {
        return tokenResponse.getRole().contains("USER");
    }

    private Optional<String> extractToken(ServerHttpRequest request) {
        List<String> authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        return authHeader != null && !authHeader.isEmpty() ?
                Optional.of(authHeader.get(0).replace("Bearer ", "")) :
                Optional.empty();
    }

    private Mono<Void> unauthenticated(ServerHttpResponse response) {
        return createErrorResponse(response, HttpStatus.UNAUTHORIZED, "Unauthenticated");
    }

    private Mono<Void> unauthorized(ServerHttpResponse response) {
        return createErrorResponse(response, HttpStatus.FORBIDDEN, "Forbidden");
    }

    private Mono<Void> createErrorResponse(ServerHttpResponse response, HttpStatus status, String message) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(message)
                .build();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(apiResponse.toString().getBytes(StandardCharsets.UTF_8))));
    }


    @SuppressWarnings("unchecked")
    private <T> Mono<T> handleError(ServerHttpResponse response) {
        // Log the error for debugging purposes
        // logger.error("Error occurred: {}", e.getMessage());
        return (Mono<T>) createErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}


