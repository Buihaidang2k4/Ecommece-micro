package com.myshop.core.entity.customer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "ward_code", length = 20)
    private String wardCode;

    @Column(name = "ward", length = 100)
    private String ward;

    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "additional_info", length = 500)
    private String additionalInfo;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "type")
    private Integer type;

    @Column(name = "label", length = 50)
    private String label;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
