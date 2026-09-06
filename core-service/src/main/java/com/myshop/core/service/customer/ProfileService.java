package com.myshop.core.service.customer;

import com.myshop.commons.exception.BusinessException;
import com.myshop.core.constant.CoreMessageKeys;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.dto.request.ProfileRequest;
import com.myshop.core.dto.response.ProfileResponse;
import com.myshop.core.entity.customer.UserProfile;
import com.myshop.core.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository userProfileRepository;

    public ProfileResponse getByProfileId(Long profileId) {
        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PROFILE_NOT_FOUND)
                ));
        return toResponse(profile);
    }

    public ProfileResponse getByUserId(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PROFILE_NOT_FOUND)
                ));
        return toResponse(profile);
    }

    public ProfileResponse getMyProfile(Long userId) {
        return getByUserId(userId);
    }

    @Transactional
    public ProfileResponse create(ProfileRequest request) {
        if (userProfileRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.PROFILE_ALREADY_EXISTS)
            );
        }
        UserProfile profile = UserProfile.builder()
                .userId(request.getUserId())
                .username(request.getUsername())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .mobileNumber(request.getMobileNumber())
                .build();
        userProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse update(Long profileId, ProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PROFILE_NOT_FOUND)
                ));
        if (request.getUsername() != null) profile.setUsername(request.getUsername());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getBirthDate() != null) profile.setBirthDate(request.getBirthDate());
        if (request.getMobileNumber() != null) profile.setMobileNumber(request.getMobileNumber());
        userProfileRepository.save(profile);
        return toResponse(profile);
    }

    private ProfileResponse toResponse(UserProfile p) {
        return ProfileResponse.builder()
                .profileId(p.getProfileId())
                .userId(p.getUserId())
                .username(p.getUsername())
                .gender(p.getGender())
                .birthDate(p.getBirthDate())
                .mobileNumber(p.getMobileNumber())
                .build();
    }
}
