package com.myshop.auth.controller;

import com.myshop.auth.dto.request.RoleRequest;
import com.myshop.auth.dto.response.RoleResponse;
import com.myshop.auth.service.RoleService;
import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.security.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/roles")
@RequiredArgsConstructor
@RequirePermission("role:manage")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(roleService.getRoleById(id));
    }

    @PostMapping
    public ApiResponse<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.of(200, "Role created", roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ApiResponse.of(200, "Role updated", roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.of(200, "Role deleted", null);
    }
}
