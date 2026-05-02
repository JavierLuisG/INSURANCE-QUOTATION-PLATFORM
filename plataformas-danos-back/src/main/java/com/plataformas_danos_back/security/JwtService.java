package com.plataformas_danos_back.security;

import com.plataformas_danos_back.model.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:changeme-use-env-var-in-production}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private long expirationSeconds;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

        log.debug("Generating JWT for subject={}, role={}", user.getEmail(), user.getRole());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles", List.of(user.getRole()))
                .issuedAt(now)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
