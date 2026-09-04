package com.myshop.auth.service;

import com.myshop.auth.dto.request.RoleRequest;
import com.myshop.auth.dto.response.RoleResponse;
import com.myshop.auth.entity.Permission;
import com.myshop.auth.entity.Role;
import com.myshop.auth.entity.RolePermission;
import com.myshop.auth.repository.PermissionRepository;
import com.myshop.auth.repository.RolePermissionRepository;
import com.myshop.auth.repository.RoleRepository;
import com.myshop.commons.exception.AppException;
import com.myshop.commons.exception.ErrorCode;
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

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));
        return toResponse(role);
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        roleRepository.findByRoleName(request.getRoleName()).ifPresent(r -> {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Role already exists");
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
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));

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
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found");
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
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "One or more permissions not found");
        }
        for (Permission permission : permissions) {
            rolePermissionRepository.save(RolePermission.builder()
                    .roleId(roleId)
                    .permissionId(permission.getId())
                    .build());
        }
    }

    private RoleResponse toResponse(Role role) {
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId()).stream()
                .map(RolePermission::getPermissionId)
                .toList();
        List<String> permissions = permissionIds.isEmpty()
                ? List.of()
                : permissionRepository.findAllById(permissionIds).stream()
                .map(Permission::getCode)
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}
