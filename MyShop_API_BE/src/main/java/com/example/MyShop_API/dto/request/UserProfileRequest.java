package com.example.MyShop_API.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data // Auto create getter setter constructor toString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileRequest {
    @NotNull(message = "Username cannot be null")
    @Size(min = 5, max = 30, message = "Username must be between 5 and 30 characters long")
    String username;

    @NotNull(message = "Mobile Number cannot be null")
    @Size(min = 10, max = 10, message = "Mobile Number must be exactly 10 digits long")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile Number must contain only numbers")
    String mobileNumber;

    @NotNull(message = "Gender cannot be null")
    Boolean gender;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate;
}
