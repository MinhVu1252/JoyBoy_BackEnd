package com.joyboy.userservice.repositories;

import com.joyboy.userservice.entities.models.Token;
import com.joyboy.userservice.entities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);
    Token findByRefreshToken(String refreshtoken);

    Token findByJwtId(@Param("jwtId") String jwtId);
}
