package com.ecommerce.seller.application.dto.request;

import com.ecommerce.product.domain.entity.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 등록 요청 DTO
 * 판매자가 새로운 상품을 등록할 때 사용
 */
@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    
    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다")
    private String name;
    
    @Size(max = 5000, message = "상품 설명은 5000자를 초과할 수 없습니다")
    private String description;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다")
    private BigDecimal price;
    
    @NotNull(message = "재고 수량은 필수입니다")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    private Integer stockQuantity;
    
    @NotNull(message = "카테고리는 필수입니다")
    private Long categoryId;
    
    private ProductStatus status = ProductStatus.PREPARING;
    
    // 이미지 파일들 (멀티파트)
    private List<MultipartFile> imageFiles;
    
    @Builder
    public ProductCreateRequest(String name, String description, BigDecimal price, 
                               Integer stockQuantity, Long categoryId, ProductStatus status,
                               List<MultipartFile> imageFiles) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.status = status != null ? status : ProductStatus.PREPARING;
        this.imageFiles = imageFiles;
    }
    
    /**
     * 이미지 파일이 있는지 확인
     */
    public boolean hasImageFiles() {
        return imageFiles != null && !imageFiles.isEmpty() && 
               imageFiles.stream().anyMatch(file -> !file.isEmpty());
    }
}