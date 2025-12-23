package com.ecommerce.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 찜 목록 엔티티
 * 사용자가 관심 있는 상품을 저장
 */
@Entity
@Table(name = "wishlists", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Wishlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;  // 상품 서비스의 상품 ID
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Builder
    public Wishlist(User user, Long productId) {
        this.user = user;
        this.productId = productId;
    }
    
    // === 비즈니스 로직 메서드 ===
    
    /**
     * 찜 목록에서 제거 가능 여부 확인
     */
    public boolean canRemove(Long userId) {
        return this.user.getId().equals(userId);
    }
}