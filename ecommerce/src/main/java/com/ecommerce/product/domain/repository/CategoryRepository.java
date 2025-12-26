package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 리포지토리
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 활성화된 카테고리 조회
     */
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 부모 카테고리로 하위 카테고리 조회
     */
    List<Category> findByParentIdAndIsActiveTrueOrderByDisplayOrderAsc(Long parentId);
    
    /**
     * 루트 카테고리 조회 (부모가 없는 카테고리)
     */
    List<Category> findByParentIdIsNullAndIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 카테고리명으로 조회
     */
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    /**
     * 카테고리 계층 구조 조회 (재귀 쿼리)
     */
    @Query(value = """
        WITH RECURSIVE category_tree AS (
            SELECT id, name, description, parent_id, display_order, is_active, 0 as level
            FROM categories 
            WHERE id = :categoryId AND is_active = true
            
            UNION ALL
            
            SELECT c.id, c.name, c.description, c.parent_id, c.display_order, c.is_active, ct.level + 1
            FROM categories c
            INNER JOIN category_tree ct ON c.parent_id = ct.id
            WHERE c.is_active = true
        )
        SELECT * FROM category_tree ORDER BY level, display_order
        """, nativeQuery = true)
    List<Category> findCategoryTreeById(@Param("categoryId") Long categoryId);
}