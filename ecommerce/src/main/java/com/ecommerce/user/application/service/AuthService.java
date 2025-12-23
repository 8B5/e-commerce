package com.ecommerce.user.application.service;

import com.ecommerce.common.crypto.ScpDbCryptoUtil;
import com.ecommerce.user.application.dto.request.LoginRequest;
import com.ecommerce.user.application.dto.request.SignupRequest;
import com.ecommerce.user.application.dto.response.LoginResponse;
import com.ecommerce.user.code.LoginResultCode;
import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.repository.UserRepository;
import com.ecommerce.user.infrastructure.security.JwtTokenProvider;
import com.ecommerce.user.infrastructure.security.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 인증 서비스
 * 회원가입, 로그인, 토큰 갱신 등 인증 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final ScpDbCryptoUtil cryptoUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    
    /**
     * 회원가입
     */
    @Transactional
    public LoginResultCode signup(SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            return LoginResultCode.EMAIL_ALREADY_EXISTS;
        }
        
        // 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();
        
        // 비밀번호 암호화
        user.encryptPassword(cryptoUtil);
        
        // 저장
        userRepository.save(user);
        
        log.info("새 사용자 가입: email={}, role={}", request.getEmail(), request.getRole());
        return LoginResultCode.SIGNUP_SUCCESS;
    }
    
    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException(LoginResultCode.USER_NOT_FOUND));
        
        // 계정 상태 확인
        if (!user.canLogin()) {
            if (user.isAccountLocked()) {
                throw new AuthenticationException(LoginResultCode.ACCOUNT_LOCKED);
            }
            if (!user.isEnabled()) {
                throw new AuthenticationException(LoginResultCode.ACCOUNT_DISABLED);
            }
        }
        
        // 비밀번호 검증
        if (!user.verifyPassword(request.getPassword(), cryptoUtil)) {
            user.loginFailed();
            userRepository.save(user);
            throw new AuthenticationException(LoginResultCode.PASSWORD_MISMATCH);
        }
        
        // 로그인 성공 처리
        user.loginSuccess();
        userRepository.save(user);
        
        // 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        // 리프레시 토큰 저장
        Duration refreshTokenExpiration = jwtTokenProvider.getRefreshTokenExpiration();
        refreshTokenStore.store(user.getId(), refreshToken, refreshTokenExpiration);
        
        log.info("사용자 로그인 성공: userId={}, email={}", user.getId(), user.getEmail());
        
        return LoginResponse.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                accessToken,
                refreshToken,
                30 * 60  // 30분 (초 단위)
        );
    }
    
    /**
     * 토큰 갱신
     */
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new AuthenticationException(LoginResultCode.INVALID_TOKEN);
        }
        
        // 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        
        // 저장된 리프레시 토큰과 비교
        String storedRefreshToken = refreshTokenStore.findByUserId(userId)
                .orElseThrow(() -> new AuthenticationException(LoginResultCode.REFRESH_TOKEN_NOT_FOUND));
        
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new AuthenticationException(LoginResultCode.INVALID_TOKEN);
        }
        
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException(LoginResultCode.USER_NOT_FOUND));
        
        // 계정 상태 확인
        if (!user.canLogin()) {
            refreshTokenStore.delete(userId);
            if (user.isAccountLocked()) {
                throw new AuthenticationException(LoginResultCode.ACCOUNT_LOCKED);
            }
            throw new AuthenticationException(LoginResultCode.ACCOUNT_DISABLED);
        }
        
        // 새 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);
        
        // 새 리프레시 토큰 저장
        Duration refreshTokenExpiration = jwtTokenProvider.getRefreshTokenExpiration();
        refreshTokenStore.store(user.getId(), newRefreshToken, refreshTokenExpiration);
        
        log.info("토큰 갱신 성공: userId={}", user.getId());
        
        return LoginResponse.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                newAccessToken,
                newRefreshToken,
                30 * 60  // 30분 (초 단위)
        );
    }
    
    /**
     * 로그아웃
     */
    @Transactional
    public void logout(Long userId) {
        refreshTokenStore.delete(userId);
        log.info("사용자 로그아웃: userId={}", userId);
    }
    
    /**
     * 인증 예외 클래스
     */
    public static class AuthenticationException extends RuntimeException {
        private final LoginResultCode resultCode;
        
        public AuthenticationException(LoginResultCode resultCode) {
            super(resultCode.getMessage());
            this.resultCode = resultCode;
        }
        
        public LoginResultCode getResultCode() {
            return resultCode;
        }
    }
}