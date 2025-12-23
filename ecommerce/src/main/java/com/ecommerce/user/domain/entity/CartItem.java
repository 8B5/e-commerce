package com.ecommerce.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 장바구니 아이템 엔티티
 * 장바구니에 담긴 개별 상품 정보
 */
@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @Column(nullable = false)
    private Long productId;  // 상품 서비스의 상품 ID
    
    @Column(nullable = false)
    private int quantity;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Builder
    public CartItem(Cart cart, Long productId, int quantity) {
        this.cart = cart;
        this.productId = productId;
        this.quantity = quantity;
        validateQuantity(quantity);
    }
    
    // === 비즈니스 로직 메서드 ===
    
    /**
     * 수량 변경
     */
    public void updateQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }
    
    /**
     * 수량 증가
     */
    public void increaseQuantity(int amount) {
        validateQuantity(amount);
        updateQuantity(this.quantity + amount);
    }
    
    /**
     * 수량 감소
     */
    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("감소할 수량은 0보다 커야 합니다");
        }
        
        int newQuantity = this.quantity - amount;
        if (newQuantity < 1) {
            throw new IllegalArgumentException("수량은 1개 미만이 될 수 없습니다");
        }
        
        updateQuantity(newQuantity);
    }
    
    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        if (quantity > 99) {
            throw new IllegalArgumentException("수량은 99개를 초과할 수 없습니다");
        }
    }
}