package com.myshop.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String fullName;
    private String phone;
    private String street;
    private String wardCode;
    private String ward;
    private Integer districtId;
    private String district;
    private Integer provinceId;
    private String province;
    private String postalCode;
    private String additionalInfo;
    private Boolean isDefault;
    private Integer type;
    private String label;
}
