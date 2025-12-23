package com.ecommerce.user.infrastructure.security;

import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 * Access Token과 Refresh Token을 관리
 */
@Slf4j
@Component
public class JwtTokenProvider {
    
    private final SecretKey secretKey;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;
    
    public JwtTokenProvider(
            @Value("${app.jwt.secret:defaultSecretKeyForDevelopmentOnlyNotForProduction}") String secret,
            @Value("${app.jwt.access-token-expiration:PT30M}") Duration accessTokenExpiration,
            @Value("${app.jwt.refresh-token-expiration:P7D}") Duration refreshTokenExpiration) {
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
    
    /**
     * Access Token 생성
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpiration);
        
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(refreshTokenExpiration);
        
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("type", "refresh")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }
    
    /**
     * 토큰에서 권한 추출
     */
    public UserRole getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        String role = claims.get("role", String.class);
        return UserRole.valueOf(role);
    }
    
    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
    
    /**
     * Refresh Token 여부 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 토큰 만료 시간 반환
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
    
    /**
     * Refresh Token 만료 시간 반환
     */
    public Duration getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
    
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}