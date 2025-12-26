package com.ecommerce.product.domain.entity;

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
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}),
       indexes = {
           @Index(name = "idx_wishlist_user", columnList = "user_id"),
           @Index(name = "idx_wishlist_product", columnList = "product_id"),
           @Index(name = "idx_wishlist_created", columnList = "created_at")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Wishlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 연관관계 - 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Builder
    public Wishlist(Long userId, Product product) {
        this.userId = userId;
        this.product = product;
    }
    
    /**
     * 찜 목록 소유자 확인
     */
    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}