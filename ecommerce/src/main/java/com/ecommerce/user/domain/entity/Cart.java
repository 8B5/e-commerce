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
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 엔티티
 * 사용자별 장바구니 관리
 */
@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Builder
    public Cart(User user) {
        this.user = user;
    }
    
    // === 비즈니스 로직 메서드 ===
    
    /**
     * 장바구니에 상품 추가
     */
    public CartItem addItem(Long productId, int quantity) {
        validateQuantity(quantity);
        
        // 이미 존재하는 상품인지 확인
        CartItem existingItem = findItemByProductId(productId);
        if (existingItem != null) {
            existingItem.updateQuantity(existingItem.getQuantity() + quantity);
            return existingItem;
        }
        
        // 새로운 아이템 추가
        CartItem newItem = CartItem.builder()
            .cart(this)
            .productId(productId)
            .quantity(quantity)
            .build();
        
        this.items.add(newItem);
        return newItem;
    }
    
    /**
     * 장바구니 아이템 수량 변경
     */
    public void updateItemQuantity(Long productId, int quantity) {
        validateQuantity(quantity);
        
        CartItem item = findItemByProductId(productId);
        if (item == null) {
            throw new IllegalArgumentException("장바구니에 해당 상품이 없습니다");
        }
        
        item.updateQuantity(quantity);
    }
    
    /**
     * 장바구니에서 상품 제거
     */
    public void removeItem(Long productId) {
        CartItem item = findItemByProductId(productId);
        if (item != null) {
            this.items.remove(item);
        }
    }
    
    /**
     * 장바구니 전체 비우기
     */
    public void clearAll() {
        this.items.clear();
    }
    
    /**
     * 총 아이템 개수
     */
    public int getTotalItemCount() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
    
    /**
     * 장바구니가 비어있는지 확인
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    private CartItem findItemByProductId(Long productId) {
        return items.stream()
            .filter(item -> item.getProductId().equals(productId))
            .findFirst()
            .orElse(null);
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