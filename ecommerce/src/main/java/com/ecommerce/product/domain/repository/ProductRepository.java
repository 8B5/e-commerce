package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 상품 리포지토리
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // ========== 구매자용 쿼리 ==========
    
    /**
     * 구매자에게 노출 가능한 상품 조회 (ID로)
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.status IN ('SELLING', 'SOLD_OUT')")
    Optional<Product> findByIdAndVisibleToBuyer(@Param("id") Long id);
    
    /**
     * 카테고리별 상품 조회 (페이징) - 구매자용
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status IN ('SELLING', 'SOLD_OUT') ORDER BY p.createdAt DESC")
    Page<Product> findByCategoryIdAndVisibleToBuyer(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * 카테고리별 상품 조회 (하위 카테고리 포함) - 구매자용
     */
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.status IN ('SELLING', 'SOLD_OUT') ORDER BY p.createdAt DESC")
    Page<Product> findByCategoryIdsAndVisibleToBuyer(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);
    
    /**
     * 가격 범위로 상품 조회 - 구매자용
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.status IN ('SELLING', 'SOLD_OUT') ORDER BY p.createdAt DESC")
    Page<Product> findByPriceBetweenAndVisibleToBuyer(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    /**
     * 상품명으로 검색 (LIKE 검색) - 구매자용
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% AND p.status IN ('SELLING', 'SOLD_OUT') ORDER BY p.createdAt DESC")
    Page<Product> findByNameContainingAndVisibleToBuyer(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 인기 상품 조회 (리뷰 개수 기준) - 구매자용
     */
    @Query("""
        SELECT p FROM Product p 
        LEFT JOIN p.reviews r 
        WHERE p.status IN ('SELLING', 'SOLD_OUT') 
        GROUP BY p.id 
        ORDER BY COUNT(r.id) DESC, p.createdAt DESC
        """)
    Page<Product> findPopularProducts(Pageable pageable);
    
    /**
     * 최신 상품 조회 - 구매자용
     */
    @Query("SELECT p FROM Product p WHERE p.status IN ('SELLING', 'SOLD_OUT') ORDER BY p.createdAt DESC")
    Page<Product> findLatestProducts(Pageable pageable);
    
    /**
     * 재고가 있는 상품 조회 - 구매자용
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :minStock AND p.status = 'SELLING' ORDER BY p.createdAt DESC")
    Page<Product> findByStockQuantityGreaterThanAndSelling(@Param("minStock") Integer minStock, Pageable pageable);
    
    /**
     * 복합 검색 (상품명, 설명 포함) - 구매자용
     */
    @Query("""
        SELECT p FROM Product p 
        WHERE (p.name LIKE %:keyword% OR p.description LIKE %:keyword%) 
        AND p.status IN ('SELLING', 'SOLD_OUT') 
        ORDER BY p.createdAt DESC
        """)
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    // ========== 판매자용 쿼리 ==========
    
    /**
     * 판매자별 상품 조회 (특정 상태)
     */
    Page<Product> findBySellerIdAndStatusOrderByCreatedAtDesc(Long sellerId, ProductStatus status, Pageable pageable);
    
    /**
     * 판매자별 상품 조회 (삭제된 것 제외)
     */
    Page<Product> findBySellerIdAndStatusNotOrderByCreatedAtDesc(Long sellerId, ProductStatus excludeStatus, Pageable pageable);
    
    /**
     * 판매자별 상품 개수 (특정 상태)
     */
    Long countBySellerIdAndStatus(Long sellerId, ProductStatus status);
    
    /**
     * 판매자별 상품 개수 (특정 상태 제외)
     */
    Long countBySellerIdAndStatusNot(Long sellerId, ProductStatus excludeStatus);
    
    /**
     * 판매자의 재고 부족 상품 개수
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.sellerId = :sellerId AND p.stockQuantity <= :threshold AND p.status != 'DELETED'")
    Long countLowStockProducts(@Param("sellerId") Long sellerId, @Param("threshold") Integer threshold);
    
    /**
     * 판매자의 총 재고 가치 계산
     */
    @Query("SELECT SUM(p.price * p.stockQuantity) FROM Product p WHERE p.sellerId = :sellerId AND p.status != 'DELETED'")
    BigDecimal calculateTotalStockValue(@Param("sellerId") Long sellerId);
    
    /**
     * 판매자가 사용 중인 카테고리 개수
     */
    @Query("SELECT COUNT(DISTINCT p.category.id) FROM Product p WHERE p.sellerId = :sellerId AND p.status != 'DELETED'")
    Long countDistinctCategoriesBySeller(@Param("sellerId") Long sellerId);
}