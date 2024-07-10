package com.joyboy.userservice.config.jwt;

import com.joyboy.commonservice.common.exceptions.ExpiredTokenException;
import com.joyboy.commonservice.common.exceptions.InvalidParamException;
import com.joyboy.userservice.entities.models.Role;
import com.joyboy.userservice.entities.models.Token;
import com.joyboy.userservice.entities.models.User;
import com.joyboy.userservice.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretkey}")
    private String secretKey;

    private final TokenRepository tokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(User user) throws Exception{
        Map<String, Object> claims = new HashMap<>();
        String subject = getSubject(user);
        claims.put("subject", subject);
        claims.put("userId", user.getId());
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getNameRole)
                .collect(Collectors.toList());
        claims.put("roles", roleNames);
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .setId(UUID.randomUUID().toString())
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: "+e.getMessage());
        }
    }

    private static String getSubject(User user) {
        String subject = user.getUsername();
        if (subject == null || subject.isBlank()) {
            subject = user.getEmail();
        }
        return subject;
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    public String extractJwtId(String token) {
        return extractClaim(token, Claims::getId);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String getSubject(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User userDetails) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            return isTokenValid(token, subject, userDetails);
        } catch (JwtException | IllegalArgumentException e) {
            logJwtException(e);
            return false;
        }
    }

    private boolean isTokenValid(String token, String subject, User userDetails) {
        Token existingToken = tokenRepository.findByToken(token);
        if (existingToken == null || existingToken.isRevoked() || !userDetails.isActive()) {
            return false;
        }
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private static final Map<Class<? extends Exception>, String> JWT_EXCEPTION_MESSAGES = Map.of(
            MalformedJwtException.class, "Invalid JWT token: ",
            ExpiredJwtException.class, "JWT token is expired: ",
            UnsupportedJwtException.class, "JWT token is unsupported: ",
            IllegalArgumentException.class, "JWT claims string is empty: "
    );

    private void logJwtException(Exception e) {
        String message = JWT_EXCEPTION_MESSAGES.getOrDefault(e.getClass(), "JWT exception: ");
        logger.error("{}{}", message, e.getMessage());
    }

    public Claims verifyToken(String token, boolean isRefresh) throws Exception {
        try {
            extractAllClaims(token);

            isTokenExpired(token);

            // Check token is revoked
            Token existingToken = tokenRepository.findByToken(token);
            if(existingToken == null || existingToken.isRevoked() || existingToken.isExpired()) {
                throw new ExpiredTokenException("Token is revoked");
            }

            return extractAllClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new Exception("AUTHENTICATE", e);
        }
    }
}
