package com.ecommerce.user.domain.repository;

import com.ecommerce.user.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 장바구니 Repository
 */
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * 사용자별 장바구니 조회 (아이템 포함)
     */
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);
    
    /**
     * 사용자별 장바구니 조회
     */
    Optional<Cart> findByUserId(Long userId);
    
    /**
     * 사용자 장바구니 존재 여부 확인
     */
    boolean existsByUserId(Long userId);
}