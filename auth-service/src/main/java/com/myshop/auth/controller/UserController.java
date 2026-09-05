package com.myshop.auth.controller;

import com.myshop.auth.constant.ApiPath;
import com.myshop.auth.dto.request.UpdateUserRolesRequest;
import com.myshop.auth.dto.request.UserRegistrationRequest;
import com.myshop.auth.dto.response.UserListRow;
import com.myshop.auth.dto.response.UserResponse;
import com.myshop.auth.service.UserService;
import com.myshop.commons.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ApiResponse.of(200, "User registered", userService.register(request));
    }

    @GetMapping
    public ApiResponse<List<UserListRow>> listUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String roleName
    ) {
        return ApiResponse.of(200, "Success", userService.listUsers(email, enabled, roleName));
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> myInfo() {
        return ApiResponse.of(200, "Success", userService.getMyInfo());
    }

    @PutMapping("/{userId}/lock-user")
    public ApiResponse<Long> lockUser(
            @PathVariable Long userId,
            @RequestParam("lockReason") @NotBlank String lockReason
    ) {
        userService.lockUser(userId, lockReason);
        return ApiResponse.of(200, "User locked", userId);
    }

    @PutMapping("/{userId}/unlocked-user")
    public ApiResponse<Long> unlockUser(@PathVariable Long userId) {
        userService.unlockUser(userId);
        return ApiResponse.of(200, "User unlocked", userId);
    }

    @PutMapping("/{id}/update-role-user")
    public ApiResponse<UserResponse> updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRolesRequest request
    ) {
        return ApiResponse.of(200, "Roles updated", userService.updateRoles(id, request));
    }
}
