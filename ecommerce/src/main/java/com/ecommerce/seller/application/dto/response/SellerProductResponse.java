package com.ecommerce.seller.application.dto.response;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 판매자용 상품 응답 DTO
 * 판매자가 자신의 상품을 조회할 때 사용 (구매자용보다 더 많은 정보 포함)
 */
@Getter
@Builder
public class SellerProductResponse {
    
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private ProductStatus status;
    private String statusDisplayName;
    private Long categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Entity에서 DTO로 변환
     */
    public static SellerProductResponse from(Product product) {
        return SellerProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .status(product.getStatus())
                .statusDisplayName(product.getStatus().getDisplayName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .imageUrls(product.getImageUrls())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}