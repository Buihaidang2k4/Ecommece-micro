package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long profileId;
    private Long userId;
    private String username;
    private String gender;
    private LocalDate birthDate;
    private String mobileNumber;
}
