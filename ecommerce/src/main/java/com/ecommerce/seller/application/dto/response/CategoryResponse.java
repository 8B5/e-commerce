package com.ecommerce.seller.application.dto.response;

import com.ecommerce.product.domain.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 응답 DTO
 */
@Getter
@Builder
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private Integer displayOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 하위 카테고리들
    private List<CategoryResponse> children;
    
    // 상품 개수
    private Long productCount;
    
    /**
     * Entity에서 DTO로 변환 (하위 카테고리 포함)
     */
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .children(category.getChildren().stream()
                        .map(CategoryResponse::from)
                        .collect(Collectors.toList()))
                .productCount((long) category.getProducts().size())
                .build();
    }
    
    /**
     * Entity에서 DTO로 변환 (단순 변환, 하위 카테고리 제외)
     */
    public static CategoryResponse fromSimple(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParentId())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .productCount((long) category.getProducts().size())
                .build();
    }
}