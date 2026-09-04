package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên quá dài")
    String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$",
            message = "Số điện thoại không hợp lệ")
    String phone;

    @NotBlank(message = "Số nhà, tên đường không được để trống")
    @Size(max = 255)
    String street;

    Integer wardCode;

    @NotBlank(message = "Phường/Xã không được để trống")
    String ward;

    Integer districtID;

    @NotBlank(message = "Quận/Huyện không được để trống")
    String district;

    Integer provinceID;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    String province;
    String postalCode;

    @Size(max = 500, message = "Ghi chú quá dài")
    String additionalInfo;

    @Builder.Default
    Boolean isDefault = false;

    String type; // "HOME", "WORK", "OTHER"

    @Size(max = 50)
    String label; // Ví dụ: "Nhà riêng", "Công ty", "Nhà bố mẹ", "Quán cà phê"

    LocalDateTime createdAt = LocalDateTime.now();
}