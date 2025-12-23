package com.ecommerce.user.domain.repository;

import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 활성화된 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
    
    /**
     * 권한별 사용자 목록 조회
     */
    List<User> findByRole(UserRole role);
    
    /**
     * 잠긴 계정 목록 조회
     */
    List<User> findByAccountLockedTrue();
    
    /**
     * 특정 기간 이후 로그인한 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since")
    List<User> findUsersLoggedInSince(@Param("since") LocalDateTime since);
    
    /**
     * 비활성화된 사용자 조회
     */
    List<User> findByEnabledFalse();
}