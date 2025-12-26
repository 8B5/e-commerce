package com.ecommerce.seller.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카테고리 생성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class CategoryCreateRequest {
    
    @NotBlank(message = "카테고리명은 필수입니다")
    @Size(max = 100, message = "카테고리명은 100자를 초과할 수 없습니다")
    private String name;
    
    @Size(max = 500, message = "카테고리 설명은 500자를 초과할 수 없습니다")
    private String description;
    
    private Long parentId;
    
    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    private Integer displayOrder = 0;
    
    @Builder
    public CategoryCreateRequest(String name, String description, Long parentId, Integer displayOrder) {
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }
}