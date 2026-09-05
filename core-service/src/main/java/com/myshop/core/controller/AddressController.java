package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.AddressRequest;
import com.myshop.core.dto.response.AddressResponse;
import com.myshop.core.service.customer.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.ADDRESSES)
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/by-profile/{profileId}")
    public ApiResponse<List<AddressResponse>> getByProfileId(@PathVariable Long profileId) {
        return ApiResponse.ok(addressService.getByProfileId(profileId));
    }

    @PostMapping("/profile/{profileId}")
    public ApiResponse<AddressResponse> create(@PathVariable Long profileId,
                                               @RequestBody AddressRequest request) {
        return ApiResponse.ok(addressService.create(profileId, request));
    }

    @PutMapping("/{addressId}")
    public ApiResponse<AddressResponse> update(@PathVariable Long addressId,
                                               @RequestBody AddressRequest request) {
        return ApiResponse.ok(addressService.update(addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> delete(@PathVariable Long addressId) {
        addressService.delete(addressId);
        return ApiResponse.ok(null);
    }
}
