package com.myshop.auth.repository;

import com.myshop.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query(value = """
            SELECT DISTINCT p.code FROM permissions p
            INNER JOIN role_permissions rp ON rp.permission_id = p.id
            INNER JOIN user_roles ur ON ur.role_id = rp.role_id
            WHERE ur.user_id = :userId
            """, nativeQuery = true)
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);
}
