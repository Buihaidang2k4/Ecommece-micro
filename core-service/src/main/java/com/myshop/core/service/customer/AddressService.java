package com.myshop.core.service.customer;

import com.myshop.commons.constants.enums.CommonEnums.AddressType;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.dto.request.AddressRequest;
import com.myshop.core.dto.response.AddressResponse;
import com.myshop.core.entity.customer.Address;
import com.myshop.core.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<AddressResponse> getByProfileId(Long profileId) {
        return addressRepository.findByProfileId(profileId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AddressResponse create(Long profileId, AddressRequest request) {
        Address address = Address.builder()
                .profileId(profileId)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .street(request.getStreet())
                .wardCode(request.getWardCode())
                .ward(request.getWard())
                .districtId(request.getDistrictId())
                .district(request.getDistrict())
                .provinceId(request.getProvinceId())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .additionalInfo(request.getAdditionalInfo())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .type(request.getType() != null ? request.getType() : AddressType.HOME)
                .label(request.getLabel())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        addressRepository.save(address);
        return toResponse(address);
    }

    @Transactional
    public AddressResponse update(Long addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Core.ADDRESS_NOT_FOUND)));
        if (request.getFullName() != null) address.setFullName(request.getFullName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getStreet() != null) address.setStreet(request.getStreet());
        if (request.getWardCode() != null) address.setWardCode(request.getWardCode());
        if (request.getWard() != null) address.setWard(request.getWard());
        if (request.getDistrictId() != null) address.setDistrictId(request.getDistrictId());
        if (request.getDistrict() != null) address.setDistrict(request.getDistrict());
        if (request.getProvinceId() != null) address.setProvinceId(request.getProvinceId());
        if (request.getProvince() != null) address.setProvince(request.getProvince());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getAdditionalInfo() != null) address.setAdditionalInfo(request.getAdditionalInfo());
        if (request.getIsDefault() != null) address.setIsDefault(request.getIsDefault());
        if (request.getType() != null) address.setType(request.getType());
        if (request.getLabel() != null) address.setLabel(request.getLabel());
        address.setUpdatedAt(LocalDateTime.now());
        addressRepository.save(address);
        return toResponse(address);
    }

    @Transactional
    public void delete(Long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Core.ADDRESS_NOT_FOUND));
        }
        addressRepository.deleteById(addressId);
    }

    private AddressResponse toResponse(Address a) {
        return AddressResponse.builder()
                .addressId(a.getAddressId())
                .profileId(a.getProfileId())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .street(a.getStreet())
                .wardCode(a.getWardCode())
                .ward(a.getWard())
                .districtId(a.getDistrictId())
                .district(a.getDistrict())
                .provinceId(a.getProvinceId())
                .province(a.getProvince())
                .postalCode(a.getPostalCode())
                .additionalInfo(a.getAdditionalInfo())
                .isDefault(a.getIsDefault())
                .type(a.getType())
                .label(a.getLabel())
                .build();
    }
}
