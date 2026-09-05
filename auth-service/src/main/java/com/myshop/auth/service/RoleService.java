package com.myshop.auth.service;

import com.myshop.auth.dto.request.RoleRequest;
import com.myshop.auth.dto.response.RoleResponse;
import com.myshop.auth.entity.Permission;
import com.myshop.auth.entity.Role;
import com.myshop.auth.entity.RolePermission;
import com.myshop.auth.mapper.PermissionMapper;
import com.myshop.auth.repository.PermissionRepository;
import com.myshop.auth.repository.RolePermissionRepository;
import com.myshop.auth.repository.RoleRepository;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.ROLE_NOT_FOUND)
                ));
        return toResponse(role);
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        roleRepository.findByRoleName(request.getRoleName()).ifPresent(r -> {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.ROLE_ALREADY_EXISTS)
            );
        });

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .build();
        role = roleRepository.save(role);
        assignPermissions(role.getId(), request.getPermissionIds());
        return toResponse(role);
    }

    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.ROLE_NOT_FOUND)
                ));

        if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        role = roleRepository.save(role);

        if (request.getPermissionIds() != null) {
            rolePermissionRepository.deleteByRoleId(role.getId());
            assignPermissions(role.getId(), request.getPermissionIds());
        }
        return toResponse(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.ROLE_NOT_FOUND)
            );
        }
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
    }

    private void assignPermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.PERMISSIONS_NOT_FOUND)
            );
        }
        for (Permission permission : permissions) {
            rolePermissionRepository.save(RolePermission.builder()
                    .roleId(roleId)
                    .permissionId(permission.getId())
                    .build());
        }
    }

    private RoleResponse toResponse(Role role) {
        List<String> permissions = permissionMapper.findPermissionCodesByRoleId(role.getId());
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}
