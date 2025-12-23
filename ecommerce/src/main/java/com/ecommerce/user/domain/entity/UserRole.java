package com.ecommerce.user.domain.entity;

/**
 * 사용자 권한 열거형
 * Spring Security와 연동하여 권한 기반 접근 제어에 사용
 */
public enum UserRole {
    ROLE_BUYER("구매자"),
    ROLE_SELLER("판매자"), 
    ROLE_ADMIN("관리자");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Spring Security Authority 형식으로 반환
     */
    public String getAuthority() {
        return this.name();
    }
}