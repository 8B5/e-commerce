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
 * 상품 수정 요청 DTO
 * 부분 수정(Patch)을 지원하여 null이 아닌 필드만 업데이트
 */
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {
    
    @Size(max = 200, message = "상품명은 200자를 초과할 수 없습니다")
    private String name;
    
    @Size(max = 5000, message = "상품 설명은 5000자를 초과할 수 없습니다")
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다")
    private BigDecimal price;
    
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    private Integer stockQuantity;
    
    private Long categoryId;
    
    private ProductStatus status;
    
    // 새로 업로드할 이미지 파일들
    private List<MultipartFile> newImageFiles;
    
    // 삭제할 기존 이미지 URL들
    private List<String> deleteImageUrls;
    
    // 이미지를 모두 교체할지 여부
    private Boolean replaceAllImages = false;
    
    @Builder
    public ProductUpdateRequest(String name, String description, BigDecimal price, 
                               Integer stockQuantity, Long categoryId, ProductStatus status,
                               List<MultipartFile> newImageFiles, List<String> deleteImageUrls,
                               Boolean replaceAllImages) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.status = status;
        this.newImageFiles = newImageFiles;
        this.deleteImageUrls = deleteImageUrls;
        this.replaceAllImages = replaceAllImages != null ? replaceAllImages : false;
    }
    
    /**
     * 새로운 이미지 파일이 있는지 확인
     */
    public boolean hasNewImageFiles() {
        return newImageFiles != null && !newImageFiles.isEmpty() && 
               newImageFiles.stream().anyMatch(file -> !file.isEmpty());
    }
    
    /**
     * 삭제할 이미지가 있는지 확인
     */
    public boolean hasDeleteImageUrls() {
        return deleteImageUrls != null && !deleteImageUrls.isEmpty();
    }
    
    /**
     * 이미지 관련 변경사항이 있는지 확인
     */
    public boolean hasImageChanges() {
        return hasNewImageFiles() || hasDeleteImageUrls() || replaceAllImages;
    }
}