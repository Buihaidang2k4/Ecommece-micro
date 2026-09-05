package com.myshop.core.controller;

import com.myshop.commons.constants.JwtConstants;
import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.request.ProfileRequest;
import com.myshop.core.dto.response.ProfileResponse;
import com.myshop.core.service.customer.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PROFILES)
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.ok(profileService.getMyProfile(requireUserId(jwt)));
    }

    @GetMapping("/{profileId}")
    public ApiResponse<ProfileResponse> getById(@PathVariable Long profileId) {
        return ApiResponse.ok(profileService.getByProfileId(profileId));
    }

    @GetMapping("/by-user/{userId}")
    public ApiResponse<ProfileResponse> getByUserId(@PathVariable Long userId) {
        return ApiResponse.ok(profileService.getByUserId(userId));
    }

    @PostMapping
    public ApiResponse<ProfileResponse> create(@RequestBody ProfileRequest request) {
        return ApiResponse.ok(profileService.create(request));
    }

    @PutMapping("/{profileId}")
    public ApiResponse<ProfileResponse> update(@PathVariable Long profileId,
                                               @RequestBody ProfileRequest request) {
        return ApiResponse.ok(profileService.update(profileId, request));
    }

    private Long requireUserId(Jwt jwt) {
        Object claim = jwt.getClaim(JwtConstants.CLAIM_USER_ID);
        if (claim == null) {
            throw new BusinessException(
                    ErrorCode.UNAUTHENTICATED,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Common.UNAUTHENTICATED)
            );
        }
        return Long.valueOf(claim.toString());
    }
}
