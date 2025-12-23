package com.ecommerce.user.infrastructure.security;

import java.time.Duration;
import java.util.Optional;

/**
 * 리프레시 토큰 저장소 인터페이스
 * Redis, Database 등 다양한 구현체로 교체 가능하도록 추상화
 */
public interface RefreshTokenStore {
    
    /**
     * 리프레시 토큰 저장
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰
     * @param expiration 만료 시간
     */
    void store(Long userId, String refreshToken, Duration expiration);
    
    /**
     * 리프레시 토큰 조회
     * @param userId 사용자 ID
     * @return 리프레시 토큰
     */
    Optional<String> findByUserId(Long userId);
    
    /**
     * 리프레시 토큰으로 사용자 ID 조회
     * @param refreshToken 리프레시 토큰
     * @return 사용자 ID
     */
    Optional<Long> findUserIdByToken(String refreshToken);
    
    /**
     * 리프레시 토큰 삭제
     * @param userId 사용자 ID
     */
    void delete(Long userId);
    
    /**
     * 리프레시 토큰 존재 여부 확인
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean exists(Long userId);
    
    /**
     * 만료된 토큰 정리
     */
    void cleanupExpiredTokens();
}