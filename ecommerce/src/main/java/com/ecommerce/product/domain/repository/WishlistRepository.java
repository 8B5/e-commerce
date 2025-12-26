package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 찜 목록 리포지토리
 */
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    /**
     * 사용자별 찜 목록 조회 (페이징)
     */
    @Query("SELECT w FROM Wishlist w JOIN FETCH w.product p WHERE w.userId = :userId AND p.isActive = true ORDER BY w.createdAt DESC")
    Page<Wishlist> findByUserIdWithActiveProducts(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 사용자별 찜 목록 조회 (리스트)
     */
    @Query("SELECT w FROM Wishlist w JOIN FETCH w.product p WHERE w.userId = :userId AND p.isActive = true ORDER BY w.createdAt DESC")
    List<Wishlist> findByUserIdWithActiveProducts(@Param("userId") Long userId);
    
    /**
     * 특정 사용자의 특정 상품 찜 여부 확인
     */
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 특정 사용자의 특정 상품 찜 여부 확인 (boolean)
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 사용자별 찜 개수 조회
     */
    Long countByUserId(Long userId);
    
    /**
     * 상품별 찜 개수 조회
     */
    Long countByProductId(Long productId);
    
    /**
     * 사용자의 찜 목록에서 특정 상품 제거
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 사용자의 모든 찜 목록 제거
     */
    void deleteByUserId(Long userId);
    
    /**
     * 인기 찜 상품 조회 (찜 개수 기준)
     */
    @Query("""
        SELECT w.product.id, COUNT(w) as wishCount
        FROM Wishlist w 
        JOIN w.product p 
        WHERE p.isActive = true 
        GROUP BY w.product.id 
        ORDER BY wishCount DESC
        """)
    Page<Object[]> findPopularWishlistProducts(Pageable pageable);
    
    /**
     * 특정 기간 내 찜한 상품 조회
     */
    @Query("""
        SELECT w FROM Wishlist w 
        JOIN FETCH w.product p 
        WHERE w.userId = :userId 
        AND w.createdAt >= :startDate 
        AND p.isActive = true 
        ORDER BY w.createdAt DESC
        """)
    List<Wishlist> findRecentWishlistByUserId(@Param("userId") Long userId, @Param("startDate") java.time.LocalDateTime startDate);
}