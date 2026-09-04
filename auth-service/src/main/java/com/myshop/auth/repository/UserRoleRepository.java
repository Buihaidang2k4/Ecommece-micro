package com.myshop.auth.repository;

import com.myshop.auth.entity.UserRole;
import com.myshop.auth.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);
}
