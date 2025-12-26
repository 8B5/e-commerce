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
 * 상품 카테고리 엔티티
 * 계층형 구조를 지원하여 대분류/중분류/소분류 관리
 */
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 - 하위 카테고리들
    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();
    
    // 연관관계 - 상품들
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    @Builder
    public Category(String name, String description, Long parentId, Integer displayOrder, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.isActive = isActive != null ? isActive : true;
    }
    
    /**
     * 카테고리 활성화/비활성화
     */
    public void updateActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * 카테고리 정보 업데이트
     */
    public void updateInfo(String name, String description, Integer displayOrder) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (displayOrder != null) {
            this.displayOrder = displayOrder;
        }
    }
    
    /**
     * 루트 카테고리 여부 확인
     */
    public boolean isRootCategory() {
        return this.parentId == null;
    }
}