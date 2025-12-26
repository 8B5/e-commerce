package com.ecommerce.product.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상품 엔티티
 * 구매자가 조회하는 상품 정보를 관리
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_seller", columnList = "seller_id"),
    @Index(name = "idx_product_created", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.PREPARING;
    
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 - 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    // 연관관계 - 리뷰들
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
    
    @Builder
    public Product(String name, String description, BigDecimal price, Integer stockQuantity, 
                   Long sellerId, Category category, List<String> imageUrls, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.sellerId = sellerId;
        this.category = category;
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
        this.status = status != null ? status : ProductStatus.PREPARING;
    }
    
    /**
     * 상품 정보 업데이트 (부분 수정 지원)
     */
    public void updateInfo(String name, String description, BigDecimal price, Category category) {
        validateEditable();
        
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            this.price = price;
        }
        if (category != null) {
            this.category = category;
        }
    }
    
    /**
     * 재고 수량 업데이트
     */
    public void updateStock(Integer stockQuantity) {
        validateEditable();
        
        if (stockQuantity != null && stockQuantity >= 0) {
            this.stockQuantity = stockQuantity;
            
            // 재고에 따른 상태 자동 변경
            if (stockQuantity == 0 && this.status == ProductStatus.SELLING) {
                this.status = ProductStatus.SOLD_OUT;
            } else if (stockQuantity > 0 && this.status == ProductStatus.SOLD_OUT) {
                this.status = ProductStatus.SELLING;
            }
        }
    }
    
    /**
     * 상품 상태 변경
     */
    public void changeStatus(ProductStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("상품 상태를 %s에서 %s로 변경할 수 없습니다.", 
                    this.status.getDisplayName(), newStatus.getDisplayName())
            );
        }
        this.status = newStatus;
    }
    
    /**
     * 상품 이미지 업데이트
     */
    public void updateImages(List<String> imageUrls) {
        validateEditable();
        
        this.imageUrls.clear();
        if (imageUrls != null) {
            this.imageUrls.addAll(imageUrls);
        }
    }
    
    /**
     * 상품 소프트 삭제
     */
    public void delete() {
        this.status = ProductStatus.DELETED;
    }
    
    /**
     * 재고 확인
     */
    public boolean hasStock() {
        return this.stockQuantity > 0;
    }
    
    /**
     * 구매 가능 여부 확인
     */
    public boolean isAvailableForPurchase() {
        return this.status.isSellable() && hasStock();
    }
    
    /**
     * 구매자에게 노출 가능 여부 확인
     */
    public boolean isVisibleToBuyer() {
        return this.status.isVisibleToBuyer();
    }
    
    /**
     * 판매자 소유권 확인
     */
    public boolean isOwnedBy(Long sellerId) {
        return this.sellerId.equals(sellerId);
    }
    
    /**
     * 평균 평점 계산 (리뷰 기반)
     */
    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .filter(review -> !review.getIsDeleted())
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 리뷰 개수
     */
    public int getReviewCount() {
        return (int) reviews.stream()
                .filter(review -> !review.getIsDeleted())
                .count();
    }
    
    /**
     * 수정 가능 여부 검증
     */
    private void validateEditable() {
        if (!this.status.isEditable()) {
            throw new IllegalStateException("삭제된 상품은 수정할 수 없습니다.");
        }
    }
}