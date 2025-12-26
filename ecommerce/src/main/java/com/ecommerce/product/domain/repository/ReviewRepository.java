package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 리포지토리
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * 상품별 리뷰 조회 (삭제되지 않은 것만)
     */
    Page<Review> findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    /**
     * 사용자별 리뷰 조회
     */
    Page<Review> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 특정 사용자의 특정 상품 리뷰 조회
     */
    Optional<Review> findByProductIdAndUserIdAndIsDeletedFalse(Long productId, Long userId);
    
    /**
     * 별점별 리뷰 조회
     */
    Page<Review> findByProductIdAndRatingAndIsDeletedFalseOrderByCreatedAtDesc(
            Long productId, Integer rating, Pageable pageable);
    
    /**
     * 이미지가 있는 리뷰 조회
     */
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.isDeleted = false AND SIZE(r.imageUrls) > 0 ORDER BY r.createdAt DESC")
    Page<Review> findByProductIdWithImagesAndIsDeletedFalse(@Param("productId") Long productId, Pageable pageable);
    
    /**
     * 상품별 평균 평점 계산
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.isDeleted = false")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    /**
     * 상품별 별점 분포 조회
     */
    @Query("""
        SELECT r.rating, COUNT(r) 
        FROM Review r 
        WHERE r.product.id = :productId AND r.isDeleted = false 
        GROUP BY r.rating 
        ORDER BY r.rating DESC
        """)
    List<Object[]> findRatingDistributionByProductId(@Param("productId") Long productId);
    
    /**
     * 상품별 리뷰 개수 조회
     */
    Long countByProductIdAndIsDeletedFalse(Long productId);
    
    /**
     * 사용자가 해당 상품에 리뷰를 작성했는지 확인
     */
    boolean existsByProductIdAndUserIdAndIsDeletedFalse(Long productId, Long userId);
    
    /**
     * 최신 리뷰 조회 (전체 상품 대상)
     */
    Page<Review> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    // ========== 판매자용 쿼리 ==========
    
    /**
     * 판매자 상품들의 총 리뷰 개수
     */
    @Query("SELECT COUNT(r) FROM Review r JOIN r.product p WHERE p.sellerId = :sellerId AND r.isDeleted = false")
    Long countBySellerProducts(@Param("sellerId") Long sellerId);
    
    /**
     * 판매자 상품들의 평균 평점
     */
    @Query("SELECT AVG(r.rating) FROM Review r JOIN r.product p WHERE p.sellerId = :sellerId AND r.isDeleted = false")
    Double findAverageRatingBySellerProducts(@Param("sellerId") Long sellerId);
    
    /**
     * 판매자 상품들의 최근 N일 리뷰 개수
     */
    @Query("""
        SELECT COUNT(r) FROM Review r JOIN r.product p 
        WHERE p.sellerId = :sellerId 
        AND r.isDeleted = false 
        AND r.createdAt >= :startDate
        """)
    Long countRecentReviewsBySellerProducts(@Param("sellerId") Long sellerId, @Param("startDate") java.time.LocalDateTime startDate);
    
    /**
     * 판매자의 특정 상품 리뷰 조회
     */
    @Query("""
        SELECT r FROM Review r JOIN r.product p 
        WHERE p.sellerId = :sellerId 
        AND r.product.id = :productId 
        AND r.isDeleted = false 
        ORDER BY r.createdAt DESC
        """)
    Page<Review> findBySellerProductAndIsDeletedFalse(@Param("sellerId") Long sellerId, @Param("productId") Long productId, Pageable pageable);
    
    /**
     * 판매자의 모든 상품 리뷰 조회
     */
    @Query("""
        SELECT r FROM Review r JOIN r.product p 
        WHERE p.sellerId = :sellerId 
        AND r.isDeleted = false 
        ORDER BY r.createdAt DESC
        """)
    Page<Review> findBySellerProductsAndIsDeletedFalse(@Param("sellerId") Long sellerId, Pageable pageable);
}