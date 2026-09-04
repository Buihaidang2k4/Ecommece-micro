package com.myshop.auth.controller;

import com.myshop.auth.dto.request.UpdateUserRolesRequest;
import com.myshop.auth.dto.request.UserRegistrationRequest;
import com.myshop.auth.dto.response.UserResponse;
import com.myshop.auth.service.UserService;
import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.security.RequirePermission;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ApiResponse.of(200, "User registered", userService.register(request));
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> myInfo() {
        return ApiResponse.of(200, "Success", userService.getMyInfo());
    }

    @PutMapping("/{userId}/lock-user")
    @RequirePermission("user:lock")
    public ApiResponse<Long> lockUser(
            @PathVariable Long userId,
            @RequestParam("lockReason") @NotBlank String lockReason
    ) {
        userService.lockUser(userId, lockReason);
        return ApiResponse.of(200, "User locked", userId);
    }

    @PutMapping("/{userId}/unlocked-user")
    @RequirePermission("user:lock")
    public ApiResponse<Long> unlockUser(@PathVariable Long userId) {
        userService.unlockUser(userId);
        return ApiResponse.of(200, "User unlocked", userId);
    }

    @PutMapping("/{id}/update-role-user")
    @RequirePermission("user:write")
    public ApiResponse<UserResponse> updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRolesRequest request
    ) {
        return ApiResponse.of(200, "Roles updated", userService.updateRoles(id, request));
    }
}
