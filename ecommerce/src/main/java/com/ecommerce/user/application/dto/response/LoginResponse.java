package com.ecommerce.user.application.dto.response;

import com.ecommerce.user.domain.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 */
@Getter
@Builder
public class LoginResponse {
    
    private Long userId;
    private String email;
    private String name;
    private UserRole role;
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;  // 초 단위
    
    public static LoginResponse of(Long userId, String email, String name, UserRole role, 
                                   String accessToken, String refreshToken, long accessTokenExpiresIn) {
        return LoginResponse.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .build();
    }
}