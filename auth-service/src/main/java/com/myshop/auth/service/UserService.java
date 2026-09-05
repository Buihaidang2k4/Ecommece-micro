package com.myshop.auth.service;

import com.myshop.auth.dto.request.UpdateUserRolesRequest;
import com.myshop.auth.dto.request.UserRegistrationRequest;
import com.myshop.auth.dto.response.RoleResponse;
import com.myshop.auth.dto.response.UserListRow;
import com.myshop.auth.dto.response.UserResponse;
import com.myshop.auth.entity.Role;
import com.myshop.auth.entity.User;
import com.myshop.auth.entity.UserRole;
import com.myshop.auth.mapper.PermissionMapper;
import com.myshop.auth.mapper.UserQueryMapper;
import com.myshop.auth.repository.RoleRepository;
import com.myshop.auth.repository.UserRepository;
import com.myshop.auth.repository.UserRoleRepository;
import com.myshop.commons.constants.RoleConstants;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
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
    private final PermissionMapper permissionMapper;
    private final UserQueryMapper userQueryMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRegisteredEventPublisher userRegisteredEventPublisher;

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    ErrorCode.USER_EXISTED,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_EXISTED)
            );
        }

        Role userRole = roleRepository.findByRoleName(RoleConstants.USER)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_ROLE_NOT_FOUND)
                ));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(
                    ErrorCode.USER_EXISTED,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_EXISTED)
            );
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
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_EXISTED,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_NOT_FOUND)
                ));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserListRow> listUsers(String email, Boolean enabled, String roleName) {
        return userQueryMapper.findUsers(email, enabled, roleName);
    }

    @Transactional
    public void lockUser(Long userId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.LOCK_REASON_REQUIRED)
            );
        }
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User current = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_EXISTED,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_NOT_FOUND)
                ));

        if (userId.equals(current.getId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.CANNOT_LOCK_YOURSELF)
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_EXISTED,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_NOT_FOUND)
                ));

        if (userQueryMapper.userHasRole(user.getId(), RoleConstants.ADMIN)) {
            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.CANNOT_LOCK_ADMIN)
            );
        }
        if (!user.isEnabled()) {
            throw new BusinessException(
                    ErrorCode.USER_ALREADY_LOCKED,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_ALREADY_LOCKED)
            );
        }

        user.setEnabled(false);
        user.setLockedReason(reason);
        user.setLockedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_EXISTED,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_NOT_FOUND)
                ));
        if (user.isEnabled()) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_ALREADY_UNLOCKED)
            );
        }
        user.setEnabled(true);
        user.setLockedReason(null);
        user.setLockedAt(null);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateRoles(Long userId, UpdateUserRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_EXISTED,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.USER_NOT_FOUND)
                ));

        List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        if (roles.size() != request.getRoleIds().size()) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.ROLE_NOT_FOUND)
            );
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

    private UserResponse toResponse(User user) {
        List<String> roleNames = userQueryMapper.findRoleNamesByUserId(user.getId());
        List<RoleResponse> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByRoleName(roleName).orElse(null))
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
        List<String> permissions = permissionMapper.findPermissionCodesByRoleId(role.getId());
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }
}
