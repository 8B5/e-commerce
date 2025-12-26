package com.ecommerce.product.domain.entity;

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
 * 상품 리뷰 엔티티
 * 구매자가 작성한 상품 리뷰 정보를 관리
 */
@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_product", columnList = "product_id"),
    @Index(name = "idx_review_user", columnList = "user_id"),
    @Index(name = "idx_review_rating", columnList = "rating"),
    @Index(name = "idx_review_created", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 - 상품
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Builder
    public Review(Long userId, Integer rating, String content, Product product, List<String> imageUrls) {
        validateRating(rating);
        this.userId = userId;
        this.rating = rating;
        this.content = content;
        this.product = product;
        this.imageUrls = imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>();
    }
    
    /**
     * 리뷰 내용 수정
     */
    public void updateContent(Integer rating, String content) {
        if (rating != null) {
            validateRating(rating);
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
    }
    
    /**
     * 리뷰 이미지 업데이트
     */
    public void updateImages(List<String> imageUrls) {
        this.imageUrls.clear();
        if (imageUrls != null) {
            this.imageUrls.addAll(imageUrls);
        }
    }
    
    /**
     * 리뷰 삭제 (소프트 삭제)
     */
    public void delete() {
        this.isDeleted = true;
    }
    
    /**
     * 리뷰 복구
     */
    public void restore() {
        this.isDeleted = false;
    }
    
    /**
     * 별점 유효성 검증
     */
    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1~5 사이의 값이어야 합니다.");
        }
    }
    
    /**
     * 리뷰 작성자 확인
     */
    public boolean isWrittenBy(Long userId) {
        return this.userId.equals(userId);
    }
    
    /**
     * 이미지가 있는 리뷰인지 확인
     */
    public boolean hasImages() {
        return !imageUrls.isEmpty();
    }
}