package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long addressId;
    private Long profileId;
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
