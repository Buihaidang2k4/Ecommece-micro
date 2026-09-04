package com.myshop.auth.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private boolean enabled;
    private String lockedReason;
    private LocalDateTime lockedAt;
    private List<RoleResponse> roles;
}
