package com.myshop.auth.service;

import com.myshop.auth.dto.request.UpdateUserRolesRequest;
import com.myshop.auth.dto.request.UserRegistrationRequest;
import com.myshop.auth.dto.response.RoleResponse;
import com.myshop.auth.dto.response.UserResponse;
import com.myshop.auth.entity.Role;
import com.myshop.auth.entity.User;
import com.myshop.auth.entity.UserRole;
import com.myshop.auth.repository.PermissionRepository;
import com.myshop.auth.repository.RolePermissionRepository;
import com.myshop.auth.repository.RoleRepository;
import com.myshop.auth.repository.UserRepository;
import com.myshop.auth.repository.UserRoleRepository;
import com.myshop.commons.exception.AppException;
import com.myshop.commons.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegisteredEventPublisher userRegisteredEventPublisher;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "USER role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        userRoleRepository.save(UserRole.builder()
                .userId(user.getId())
                .roleId(userRole.getId())
                .build());

        userRegisteredEventPublisher.publish(user.getId(), user.getEmail());
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return toResponse(user);
    }

    @Transactional
    public void lockUser(Long userId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Lock reason is required");
        }
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User current = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (userId.equals(current.getId())) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "You cannot lock yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (hasAdminRole(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "You cannot lock ADMIN");
        }
        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        user.setEnabled(false);
        user.setLockedReason(reason);
        user.setLockedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (user.isEnabled()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "User is already unlocked");
        }
        user.setEnabled(true);
        user.setLockedReason(null);
        user.setLockedAt(null);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateRoles(Long userId, UpdateUserRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        if (roles.size() != request.getRoleIds().size()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "One or more roles not found");
        }

        userRoleRepository.deleteByUserId(userId);
        for (Role role : roles) {
            userRoleRepository.save(UserRole.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .build());
        }
        return toResponse(user);
    }

    private boolean hasAdminRole(Long userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .map(UserRole::getRoleId)
                .map(roleRepository::findById)
                .flatMap(java.util.Optional::stream)
                .anyMatch(role -> "ADMIN".equals(role.getRoleName()));
    }

    private UserResponse toResponse(User user) {
        List<RoleResponse> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(ur -> roleRepository.findById(ur.getRoleId()).orElse(null))
                .filter(role -> role != null)
                .map(this::toRoleResponse)
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .lockedReason(user.getLockedReason())
                .lockedAt(user.getLockedAt())
                .roles(roles)
                .build();
    }

    private RoleResponse toRoleResponse(Role role) {
        List<Long> permissionIds = rolePermissionRepository.findByRoleId(role.getId()).stream()
                .map(rp -> rp.getPermissionId())
                .toList();
        List<String> permissions = permissionIds.isEmpty()
                ? List.of()
                : permissionRepository.findAllById(permissionIds).stream()
                .map(p -> p.getCode())
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}
