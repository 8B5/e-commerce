package com.ecommerce.product.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 상품 상태 관리 Enum
 * Soft Delete와 상품 생명주기를 관리
 */
@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    
    PREPARING("준비중", "판매 준비 중인 상품"),
    SELLING("판매중", "정상 판매 중인 상품"),
    STOPPED("중단", "판매가 일시 중단된 상품"),
    SOLD_OUT("품절", "재고가 소진된 상품"),
    DELETED("삭제", "삭제된 상품");
    
    private final String displayName;
    private final String description;
    
    /**
     * 구매자에게 노출 가능한 상태인지 확인
     */
    public boolean isVisibleToBuyer() {
        return this == SELLING || this == SOLD_OUT;
    }
    
    /**
     * 판매 가능한 상태인지 확인
     */
    public boolean isSellable() {
        return this == SELLING;
    }
    
    /**
     * 수정 가능한 상태인지 확인
     */
    public boolean isEditable() {
        return this != DELETED;
    }
    
    /**
     * 삭제된 상태인지 확인
     */
    public boolean isDeleted() {
        return this == DELETED;
    }
    
    /**
     * 상태 전환 가능 여부 확인
     */
    public boolean canTransitionTo(ProductStatus newStatus) {
        if (this == DELETED) {
            return false; // 삭제된 상품은 상태 변경 불가
        }
        
        return switch (this) {
            case PREPARING -> newStatus == SELLING || newStatus == DELETED;
            case SELLING -> newStatus == STOPPED || newStatus == SOLD_OUT || newStatus == DELETED;
            case STOPPED -> newStatus == SELLING || newStatus == DELETED;
            case SOLD_OUT -> newStatus == SELLING || newStatus == DELETED;
            case DELETED -> false;
        };
    }
}