package com.myshop.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListRow {
    private Long id;
    private String email;
    private boolean enabled;
    private String lockedReason;
    private LocalDateTime lockedAt;
    private String roleNames;
}
