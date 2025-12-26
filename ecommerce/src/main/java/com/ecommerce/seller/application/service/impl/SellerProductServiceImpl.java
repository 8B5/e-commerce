package com.ecommerce.seller.application.service.impl;

import com.ecommerce.common.code.CommonResultCode;
import com.ecommerce.product.domain.entity.Category;
import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import com.ecommerce.product.domain.repository.CategoryRepository;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.domain.repository.ReviewRepository;
import com.ecommerce.product.infrastructure.storage.FileStorageService;
import com.ecommerce.seller.application.dto.request.ProductCreateRequest;
import com.ecommerce.seller.application.dto.request.ProductUpdateRequest;
import com.ecommerce.seller.application.dto.response.SellerDashboardResponse;
import com.ecommerce.seller.application.dto.response.SellerProductResponse;
import com.ecommerce.seller.application.service.SellerProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 판매자 상품 관리 서비스 구현체
 * 권한 검증과 비즈니스 로직을 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerProductServiceImpl implements SellerProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;
    
    @Override
    @Transactional
    public SellerProductResponse createProduct(Long sellerId, ProductCreateRequest request) {
        log.info("상품 등록 시작 - 판매자: {}, 상품명: {}", sellerId, request.getName());
        
        // 카테고리 조회 및 검증
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        
        if (!category.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 카테고리입니다.");
        }
        
        // 이미지 업로드 처리
        List<String> imageUrls = new ArrayList<>();
        if (request.hasImageFiles()) {
            imageUrls = fileStorageService.uploadFiles(request.getImageFiles(), "products");
            log.info("상품 이미지 업로드 완료 - 개수: {}", imageUrls.size());
        }
        
        // 상품 엔티티 생성
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sellerId(sellerId)
                .category(category)
                .imageUrls(imageUrls)
                .status(request.getStatus())
                .build();
        
        Product savedProduct = productRepository.save(product);
        log.info("상품 등록 완료 - ID: {}", savedProduct.getId());
        
        return SellerProductResponse.from(savedProduct);
    }
    
    @Override
    @Transactional
    public SellerProductResponse updateProduct(Long sellerId, Long productId, ProductUpdateRequest request) {
        log.info("상품 수정 시작 - 판매자: {}, 상품: {}", sellerId, productId);
        
        // 상품 조회 및 권한 검증
        Product product = getProductWithOwnershipValidation(sellerId, productId);
        
        // 카테고리 변경 처리
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
            
            if (!category.getIsActive()) {
                throw new IllegalArgumentException("비활성화된 카테고리입니다.");
            }
            
            product.updateInfo(request.getName(), request.getDescription(), request.getPrice(), category);
        } else {
            product.updateInfo(request.getName(), request.getDescription(), request.getPrice(), null);
        }
        
        // 재고 수량 업데이트
        if (request.getStockQuantity() != null) {
            product.updateStock(request.getStockQuantity());
        }
        
        // 상태 변경
        if (request.getStatus() != null) {
            product.changeStatus(request.getStatus());
        }
        
        // 이미지 처리
        if (request.hasImageChanges()) {
            handleImageUpdates(product, request);
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("상품 수정 완료 - ID: {}", updatedProduct.getId());
        
        return SellerProductResponse.from(updatedProduct);
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long sellerId, Long productId) {
        log.info("상품 삭제 시작 - 판매자: {}, 상품: {}", sellerId, productId);
        
        Product product = getProductWithOwnershipValidation(sellerId, productId);
        
        // 이미지 파일 삭제
        if (!product.getImageUrls().isEmpty()) {
            int deletedCount = fileStorageService.deleteFiles(product.getImageUrls());
            log.info("상품 이미지 삭제 완료 - 삭제된 파일 수: {}", deletedCount);
        }
        
        // 소프트 삭제
        product.delete();
        productRepository.save(product);
        
        log.info("상품 삭제 완료 - ID: {}", productId);
    }
    
    @Override
    @Transactional
    public SellerProductResponse changeProductStatus(Long sellerId, Long productId, ProductStatus status) {
        log.info("상품 상태 변경 - 판매자: {}, 상품: {}, 상태: {}", sellerId, productId, status);
        
        Product product = getProductWithOwnershipValidation(sellerId, productId);
        product.changeStatus(status);
        
        Product updatedProduct = productRepository.save(product);
        log.info("상품 상태 변경 완료 - ID: {}, 상태: {}", productId, status);
        
        return SellerProductResponse.from(updatedProduct);
    }
    
    @Override
    public Page<SellerProductResponse> getSellerProducts(Long sellerId, ProductStatus status, Pageable pageable) {
        Page<Product> products;
        
        if (status != null) {
            products = productRepository.findBySellerIdAndStatusOrderByCreatedAtDesc(sellerId, status, pageable);
        } else {
            products = productRepository.findBySellerIdAndStatusNotOrderByCreatedAtDesc(sellerId, ProductStatus.DELETED, pageable);
        }
        
        return products.map(SellerProductResponse::from);
    }
    
    @Override
    public SellerProductResponse getSellerProduct(Long sellerId, Long productId) {
        Product product = getProductWithOwnershipValidation(sellerId, productId);
        return SellerProductResponse.from(product);
    }
    
    @Override
    public SellerDashboardResponse getDashboard(Long sellerId) {
        log.info("판매자 대시보드 조회 - 판매자: {}", sellerId);
        
        // 상품 통계
        Long totalProductCount = productRepository.countBySellerIdAndStatusNot(sellerId, ProductStatus.DELETED);
        Long sellingProductCount = productRepository.countBySellerIdAndStatus(sellerId, ProductStatus.SELLING);
        Long soldOutProductCount = productRepository.countBySellerIdAndStatus(sellerId, ProductStatus.SOLD_OUT);
        Long stoppedProductCount = productRepository.countBySellerIdAndStatus(sellerId, ProductStatus.STOPPED);
        
        // 리뷰 통계
        Long totalReviewCount = reviewRepository.countBySellerProducts(sellerId);
        Double averageRating = reviewRepository.findAverageRatingBySellerProducts(sellerId);
        Long recentReviewCount = reviewRepository.countRecentReviewsBySellerProducts(sellerId, 7);
        
        // 재고 통계
        Long lowStockProductCount = productRepository.countLowStockProducts(sellerId, 10);
        BigDecimal totalStockValue = productRepository.calculateTotalStockValue(sellerId);
        
        // 카테고리 통계
        Long categoryCount = productRepository.countDistinctCategoriesBySeller(sellerId);
        
        return SellerDashboardResponse.builder()
                .totalProductCount(totalProductCount)
                .sellingProductCount(sellingProductCount)
                .soldOutProductCount(soldOutProductCount)
                .stoppedProductCount(stoppedProductCount)
                .totalReviewCount(totalReviewCount)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .recentReviewCount(recentReviewCount)
                .lowStockProductCount(lowStockProductCount)
                .totalStockValue(totalStockValue != null ? totalStockValue : BigDecimal.ZERO)
                .categoryCount(categoryCount)
                .build();
    }
    
    @Override
    @Transactional
    public SellerProductResponse updateStock(Long sellerId, Long productId, Integer stockQuantity) {
        log.info("재고 수량 업데이트 - 판매자: {}, 상품: {}, 수량: {}", sellerId, productId, stockQuantity);
        
        Product product = getProductWithOwnershipValidation(sellerId, productId);
        product.updateStock(stockQuantity);
        
        Product updatedProduct = productRepository.save(product);
        log.info("재고 수량 업데이트 완료 - ID: {}, 수량: {}", productId, stockQuantity);
        
        return SellerProductResponse.from(updatedProduct);
    }
    
    /**
     * 상품 조회 및 소유권 검증
     */
    private Product getProductWithOwnershipValidation(Long sellerId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        
        if (!product.isOwnedBy(sellerId)) {
            throw new IllegalArgumentException("해당 상품에 대한 권한이 없습니다.");
        }
        
        if (product.getStatus().isDeleted()) {
            throw new IllegalArgumentException("삭제된 상품입니다.");
        }
        
        return product;
    }
    
    /**
     * 이미지 업데이트 처리
     */
    private void handleImageUpdates(Product product, ProductUpdateRequest request) {
        List<String> currentImageUrls = new ArrayList<>(product.getImageUrls());
        
        // 모든 이미지 교체
        if (request.getReplaceAllImages()) {
            // 기존 이미지 삭제
            if (!currentImageUrls.isEmpty()) {
                fileStorageService.deleteFiles(currentImageUrls);
            }
            
            // 새 이미지 업로드
            List<String> newImageUrls = new ArrayList<>();
            if (request.hasNewImageFiles()) {
                newImageUrls = fileStorageService.uploadFiles(request.getNewImageFiles(), "products");
            }
            
            product.updateImages(newImageUrls);
            log.info("상품 이미지 전체 교체 완료 - 기존: {}, 신규: {}", currentImageUrls.size(), newImageUrls.size());
            
        } else {
            // 부분 이미지 업데이트
            
            // 삭제할 이미지 처리
            if (request.hasDeleteImageUrls()) {
                fileStorageService.deleteFiles(request.getDeleteImageUrls());
                currentImageUrls.removeAll(request.getDeleteImageUrls());
            }
            
            // 새 이미지 추가
            if (request.hasNewImageFiles()) {
                List<String> newImageUrls = fileStorageService.uploadFiles(request.getNewImageFiles(), "products");
                currentImageUrls.addAll(newImageUrls);
            }
            
            product.updateImages(currentImageUrls);
            log.info("상품 이미지 부분 업데이트 완료 - 최종 이미지 수: {}", currentImageUrls.size());
        }
    }
}