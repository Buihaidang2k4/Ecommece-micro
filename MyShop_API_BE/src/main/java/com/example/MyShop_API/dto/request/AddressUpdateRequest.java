package com.example.MyShop_API.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressUpdateRequest {

    // Tất cả các trường đều OPTIONAL → chỉ cập nhật những gì người dùng gửi

    @Size(max = 100, message = "Tên quá dài")
    String fullName;

    @Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$", message = "Số điện thoại không hợp lệ")
    String phone;

    @Size(max = 255)
    String street;

    String ward;

    String district;

    String province;

    String postalCode;

    @Size(max = 500, message = "Ghi chú quá dài")
    String additionalInfo;

    Boolean isDefault;

    String type;              // "HOME", "WORK", "OTHER"

    @Size(max = 50)
    String label;

    LocalDateTime updatedAt = LocalDateTime.now();
}