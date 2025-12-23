package com.ecommerce.user.application.service;

import com.ecommerce.common.crypto.ScpDbCryptoUtil;
import com.ecommerce.user.application.dto.request.LoginRequest;
import com.ecommerce.user.application.dto.request.SignupRequest;
import com.ecommerce.user.application.dto.response.LoginResponse;
import com.ecommerce.user.code.LoginResultCode;
import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.entity.UserRole;
import com.ecommerce.user.domain.repository.UserRepository;
import com.ecommerce.user.infrastructure.security.JwtTokenProvider;
import com.ecommerce.user.infrastructure.security.RefreshTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 테스트")
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ScpDbCryptoUtil cryptoUtil;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private RefreshTokenStore refreshTokenStore;
    
    @InjectMocks
    private AuthService authService;
    
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest(
                "test@example.com",
                "Password123!",
                "테스트사용자",
                "010-1234-5678",
                UserRole.ROLE_BUYER
        );
        
        loginRequest = new LoginRequest("test@example.com", "Password123!");
        
        testUser = User.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .name("테스트사용자")
                .phoneNumber("010-1234-5678")
                .role(UserRole.ROLE_BUYER)
                .build();
        
        // Reflection을 사용하여 ID 설정 (테스트용)
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testUser, 1L);
        } catch (Exception e) {
            // 테스트 환경에서만 사용
        }
    }
    
    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() {
        // given
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // when
        LoginResultCode result = authService.signup(signupRequest);
        
        // then
        assertEquals(LoginResultCode.SIGNUP_SUCCESS, result);
        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(cryptoUtil).hashPassword(signupRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_EmailAlreadyExists() {
        // given
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);
        
        // when
        LoginResultCode result = authService.signup(signupRequest);
        
        // then
        assertEquals(LoginResultCode.EMAIL_ALREADY_EXISTS, result);
        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(cryptoUtil.verifyPassword(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(testUser)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(testUser)).thenReturn("refreshToken");
        when(jwtTokenProvider.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        
        // when
        LoginResponse response = authService.login(loginRequest);
        
        // then
        assertNotNull(response);
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getName(), response.getName());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(cryptoUtil).verifyPassword(loginRequest.getPassword(), testUser.getPassword());
        verify(jwtTokenProvider).generateAccessToken(testUser);
        verify(jwtTokenProvider).generateRefreshToken(testUser);
        verify(refreshTokenStore).store(eq(testUser.getId()), eq("refreshToken"), any(Duration.class));
    }
    
    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_UserNotFound() {
        // given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        
        // when & then
        AuthService.AuthenticationException exception = assertThrows(
                AuthService.AuthenticationException.class,
                () -> authService.login(loginRequest)
        );
        
        assertEquals(LoginResultCode.USER_NOT_FOUND, exception.getResultCode());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(cryptoUtil, never()).verifyPassword(anyString(), anyString());
    }
    
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_PasswordMismatch() {
        // given
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(cryptoUtil.verifyPassword(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);
        
        // when & then
        AuthService.AuthenticationException exception = assertThrows(
                AuthService.AuthenticationException.class,
                () -> authService.login(loginRequest)
        );
        
        assertEquals(LoginResultCode.PASSWORD_MISMATCH, exception.getResultCode());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(cryptoUtil).verifyPassword(loginRequest.getPassword(), testUser.getPassword());
        verify(userRepository).save(testUser);  // 실패 횟수 증가를 위한 저장
    }
    
    @Test
    @DisplayName("로그인 실패 - 계정 잠금")
    void login_AccountLocked() {
        // given
        testUser.loginFailed();
        testUser.loginFailed();
        testUser.loginFailed();
        testUser.loginFailed();
        testUser.loginFailed();  // 5회 실패로 계정 잠금
        
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        
        // when & then
        AuthService.AuthenticationException exception = assertThrows(
                AuthService.AuthenticationException.class,
                () -> authService.login(loginRequest)
        );
        
        assertEquals(LoginResultCode.ACCOUNT_LOCKED, exception.getResultCode());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(cryptoUtil, never()).verifyPassword(anyString(), anyString());
    }
    
    @Test
    @DisplayName("토큰 갱신 성공")
    void refreshToken_Success() {
        // given
        String refreshToken = "validRefreshToken";
        Long userId = 1L;
        
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(refreshTokenStore.findByUserId(userId)).thenReturn(Optional.of(refreshToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(testUser)).thenReturn("newAccessToken");
        when(jwtTokenProvider.generateRefreshToken(testUser)).thenReturn("newRefreshToken");
        when(jwtTokenProvider.getRefreshTokenExpiration()).thenReturn(Duration.ofDays(7));
        
        // when
        LoginResponse response = authService.refreshToken(refreshToken);
        
        // then
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        
        verify(jwtTokenProvider).validateToken(refreshToken);
        verify(jwtTokenProvider).isRefreshToken(refreshToken);
        verify(refreshTokenStore).findByUserId(userId);
        verify(refreshTokenStore).store(any(Long.class), eq("newRefreshToken"), any(Duration.class));
    }
    
    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 토큰")
    void refreshToken_InvalidToken() {
        // given
        String invalidToken = "invalidToken";
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);
        
        // when & then
        AuthService.AuthenticationException exception = assertThrows(
                AuthService.AuthenticationException.class,
                () -> authService.refreshToken(invalidToken)
        );
        
        assertEquals(LoginResultCode.INVALID_TOKEN, exception.getResultCode());
        verify(jwtTokenProvider).validateToken(invalidToken);
    }
    
    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // given
        Long userId = 1L;
        
        // when
        authService.logout(userId);
        
        // then
        verify(refreshTokenStore).delete(userId);
    }
}