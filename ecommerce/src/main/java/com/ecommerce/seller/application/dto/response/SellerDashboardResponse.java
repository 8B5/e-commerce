package com.ecommerce.seller.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 판매자 대시보드 응답 DTO
 * 판매자의 상품 관련 통계 정보를 제공
 */
@Getter
@Builder
public class SellerDashboardResponse {
    
    // 상품 통계
    private Long totalProductCount;      // 전체 상품 수
    private Long sellingProductCount;    // 판매 중인 상품 수
    private Long soldOutProductCount;    // 품절 상품 수
    private Long stoppedProductCount;    // 판매 중단 상품 수
    
    // 리뷰 통계
    private Long totalReviewCount;       // 전체 리뷰 수
    private Double averageRating;        // 평균 별점
    private Long recentReviewCount;      // 최근 7일 리뷰 수
    
    // 재고 통계
    private Long lowStockProductCount;   // 재고 부족 상품 수 (10개 이하)
    private BigDecimal totalStockValue;  // 총 재고 가치
    
    // 카테고리 통계
    private Long categoryCount;          // 사용 중인 카테고리 수
    
    /**
     * 판매 중인 상품 비율 계산
     */
    public double getSellingProductRatio() {
        if (totalProductCount == 0) {
            return 0.0;
        }
        return (double) sellingProductCount / totalProductCount * 100;
    }
    
    /**
     * 품절 상품 비율 계산
     */
    public double getSoldOutProductRatio() {
        if (totalProductCount == 0) {
            return 0.0;
        }
        return (double) soldOutProductCount / totalProductCount * 100;
    }
}