package com.myshop.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
