package com.ecommerce.user.domain.entity;

import com.ecommerce.common.crypto.ScpDbCryptoUtil;
import com.ecommerce.common.util.MaskingUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 엔티티
 * 도메인 비즈니스 로직을 포함한 Rich Domain Model
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(nullable = false)
    private boolean accountLocked = false;
    
    private int failedLoginAttempts = 0;
    
    private LocalDateTime lastLoginAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wishlist> wishlists = new ArrayList<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Builder
    public User(String email, String password, String name, String phoneNumber, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role != null ? role : UserRole.ROLE_BUYER;
        this.enabled = true;
        this.accountLocked = false;
        this.failedLoginAttempts = 0;
    }
    
    // === 비즈니스 로직 메서드 ===
    
    /**
     * 비밀번호 암호화
     */
    public void encryptPassword(ScpDbCryptoUtil cryptoUtil) {
        this.password = cryptoUtil.hashPassword(this.password);
    }
    
    /**
     * 비밀번호 검증
     */
    public boolean verifyPassword(String rawPassword, ScpDbCryptoUtil cryptoUtil) {
        return cryptoUtil.verifyPassword(rawPassword, this.password);
    }
    
    /**
     * 로그인 성공 처리
     */
    public void loginSuccess() {
        this.failedLoginAttempts = 0;
        this.lastLoginAt = LocalDateTime.now();
        if (this.accountLocked) {
            this.accountLocked = false;
        }
    }
    
    /**
     * 로그인 실패 처리
     * 5회 실패 시 계정 잠금
     */
    public void loginFailed() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLocked = true;
        }
    }
    
    /**
     * 계정 잠금 해제
     */
    public void unlockAccount() {
        this.accountLocked = false;
        this.failedLoginAttempts = 0;
    }
    
    /**
     * 계정 비활성화
     */
    public void disableAccount() {
        this.enabled = false;
    }
    
    /**
     * 계정 활성화
     */
    public void enableAccount() {
        this.enabled = true;
    }
    
    /**
     * 로그인 가능 여부 확인
     */
    public boolean canLogin() {
        return this.enabled && !this.accountLocked;
    }
    
    /**
     * 마스킹된 이름 반환
     */
    public String getMaskedName() {
        return MaskingUtils.maskName(this.name);
    }
    
    /**
     * 마스킹된 이메일 반환
     */
    public String getMaskedEmail() {
        return MaskingUtils.maskEmail(this.email);
    }
    
    /**
     * 마스킹된 전화번호 반환
     */
    public String getMaskedPhoneNumber() {
        return MaskingUtils.maskPhoneNumber(this.phoneNumber);
    }
    
    /**
     * 권한 확인
     */
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }
    
    /**
     * 관리자 권한 확인
     */
    public boolean isAdmin() {
        return this.role == UserRole.ROLE_ADMIN;
    }
    
    /**
     * 판매자 권한 확인
     */
    public boolean isSeller() {
        return this.role == UserRole.ROLE_SELLER || this.role == UserRole.ROLE_ADMIN;
    }
}