package com.ecommerce.user.domain.repository;

import com.ecommerce.user.domain.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 찜 목록 Repository
 */
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    /**
     * 사용자별 찜 목록 조회
     */
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 특정 사용자의 특정 상품 찜 여부 확인
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 특정 사용자의 특정 상품 찜 조회
     */
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 사용자별 찜 개수 조회
     */
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 상품별 찜 개수 조회
     */
    long countByProductId(Long productId);
    
    /**
     * 사용자의 찜 목록에서 특정 상품 제거
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
}