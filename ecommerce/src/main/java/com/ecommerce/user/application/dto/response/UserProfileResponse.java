package com.ecommerce.user.application.dto.response;

import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 응답 DTO (마스킹 적용)
 */
@Getter
@Builder
public class UserProfileResponse {
    
    private Long userId;
    private String maskedEmail;
    private String maskedName;
    private String maskedPhoneNumber;
    private UserRole role;
    private boolean enabled;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .maskedEmail(user.getMaskedEmail())
                .maskedName(user.getMaskedName())
                .maskedPhoneNumber(user.getMaskedPhoneNumber())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}