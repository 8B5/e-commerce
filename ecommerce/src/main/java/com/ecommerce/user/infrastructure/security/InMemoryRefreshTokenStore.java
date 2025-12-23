package com.ecommerce.user.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 메모리 기반 리프레시 토큰 저장소
 * 개발/테스트 환경용 구현체 (운영에서는 Redis 사용 권장)
 */
@Slf4j
@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {
    
    private final Map<Long, TokenInfo> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenToUserIdMap = new ConcurrentHashMap<>();
    
    @Override
    public void store(Long userId, String refreshToken, Duration expiration) {
        // 기존 토큰 정리
        delete(userId);
        
        LocalDateTime expiresAt = LocalDateTime.now().plus(expiration);
        TokenInfo tokenInfo = new TokenInfo(refreshToken, expiresAt);
        
        tokenStore.put(userId, tokenInfo);
        tokenToUserIdMap.put(refreshToken, userId);
        
        log.debug("리프레시 토큰 저장: userId={}, expiresAt={}", userId, expiresAt);
    }
    
    @Override
    public Optional<String> findByUserId(Long userId) {
        TokenInfo tokenInfo = tokenStore.get(userId);
        if (tokenInfo == null || tokenInfo.isExpired()) {
            delete(userId);
            return Optional.empty();
        }
        return Optional.of(tokenInfo.token);
    }
    
    @Override
    public Optional<Long> findUserIdByToken(String refreshToken) {
        Long userId = tokenToUserIdMap.get(refreshToken);
        if (userId == null) {
            return Optional.empty();
        }
        
        TokenInfo tokenInfo = tokenStore.get(userId);
        if (tokenInfo == null || tokenInfo.isExpired()) {
            delete(userId);
            return Optional.empty();
        }
        
        return Optional.of(userId);
    }
    
    @Override
    public void delete(Long userId) {
        TokenInfo tokenInfo = tokenStore.remove(userId);
        if (tokenInfo != null) {
            tokenToUserIdMap.remove(tokenInfo.token);
            log.debug("리프레시 토큰 삭제: userId={}", userId);
        }
    }
    
    @Override
    public boolean exists(Long userId) {
        TokenInfo tokenInfo = tokenStore.get(userId);
        if (tokenInfo == null || tokenInfo.isExpired()) {
            delete(userId);
            return false;
        }
        return true;
    }
    
    @Override
    public void cleanupExpiredTokens() {
        tokenStore.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                tokenToUserIdMap.remove(entry.getValue().token);
                log.debug("만료된 토큰 정리: userId={}", entry.getKey());
                return true;
            }
            return false;
        });
    }
    
    private static class TokenInfo {
        private final String token;
        private final LocalDateTime expiresAt;
        
        public TokenInfo(String token, LocalDateTime expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}