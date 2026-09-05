package com.myshop.auth.controller;

import com.myshop.auth.constant.ApiPath;
import com.myshop.auth.entity.Permission;
import com.myshop.auth.mapper.PermissionMapper;
import com.myshop.auth.repository.PermissionRepository;
import com.myshop.commons.constants.JwtConstants;
import com.myshop.commons.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @GetMapping(ApiPath.ADMIN_PERMISSIONS)
    public ApiResponse<List<Permission>> listAll() {
        return ApiResponse.ok(permissionRepository.findAll());
    }

    @GetMapping(ApiPath.ME_PERMISSIONS)
    public ApiResponse<List<String>> myPermissions(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            Object permissionsClaim = jwt.getClaim(JwtConstants.CLAIM_PERMISSIONS);
            if (permissionsClaim instanceof Collection<?> perms && !perms.isEmpty()) {
                List<String> codes = perms.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
                return ApiResponse.ok(codes);
            }

            Object userIdClaim = jwt.getClaim(JwtConstants.CLAIM_USER_ID);
            if (userIdClaim != null) {
                Long userId = Long.valueOf(userIdClaim.toString());
                return ApiResponse.ok(permissionMapper.findPermissionCodesByUserId(userId));
            }
        }
        return ApiResponse.ok(List.of());
    }
}
