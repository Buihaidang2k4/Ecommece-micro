package com.myshop.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRolesRequest {

    @NotEmpty(message = "At least one role is required")
    private List<Long> roleIds;
}
