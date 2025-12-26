package com.ecommerce.seller.application.service;

import com.ecommerce.product.domain.entity.ProductStatus;
import com.ecommerce.seller.application.dto.request.ProductCreateRequest;
import com.ecommerce.seller.application.dto.request.ProductUpdateRequest;
import com.ecommerce.seller.application.dto.response.SellerDashboardResponse;
import com.ecommerce.seller.application.dto.response.SellerProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 판매자 상품 관리 서비스 인터페이스
 * DIP 원칙에 따라 인터페이스와 구현체를 분리
 */
public interface SellerProductService {
    
    /**
     * 상품 등록
     * @param sellerId 판매자 ID
     * @param request 상품 등록 요청
     * @return 등록된 상품 정보
     */
    SellerProductResponse createProduct(Long sellerId, ProductCreateRequest request);
    
    /**
     * 상품 수정 (부분 수정 지원)
     * @param sellerId 판매자 ID
     * @param productId 상품 ID
     * @param request 상품 수정 요청
     * @return 수정된 상품 정보
     */
    SellerProductResponse updateProduct(Long sellerId, Long productId, ProductUpdateRequest request);
    
    /**
     * 상품 삭제 (소프트 삭제)
     * @param sellerId 판매자 ID
     * @param productId 상품 ID
     */
    void deleteProduct(Long sellerId, Long productId);
    
    /**
     * 상품 상태 변경
     * @param sellerId 판매자 ID
     * @param productId 상품 ID
     * @param status 변경할 상태
     * @return 변경된 상품 정보
     */
    SellerProductResponse changeProductStatus(Long sellerId, Long productId, ProductStatus status);
    
    /**
     * 판매자의 상품 목록 조회
     * @param sellerId 판매자 ID
     * @param status 상품 상태 (null이면 전체)
     * @param pageable 페이징 정보
     * @return 상품 목록
     */
    Page<SellerProductResponse> getSellerProducts(Long sellerId, ProductStatus status, Pageable pageable);
    
    /**
     * 판매자의 특정 상품 조회
     * @param sellerId 판매자 ID
     * @param productId 상품 ID
     * @return 상품 정보
     */
    SellerProductResponse getSellerProduct(Long sellerId, Long productId);
    
    /**
     * 판매자 대시보드 정보 조회
     * @param sellerId 판매자 ID
     * @return 대시보드 통계 정보
     */
    SellerDashboardResponse getDashboard(Long sellerId);
    
    /**
     * 재고 수량 업데이트
     * @param sellerId 판매자 ID
     * @param productId 상품 ID
     * @param stockQuantity 새로운 재고 수량
     * @return 업데이트된 상품 정보
     */
    SellerProductResponse updateStock(Long sellerId, Long productId, Integer stockQuantity);
}