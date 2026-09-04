package com.myshop.auth.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Long id;
    private String roleName;
    private String description;
    private List<String> permissions;
}
