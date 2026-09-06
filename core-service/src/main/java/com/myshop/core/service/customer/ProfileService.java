package com.myshop.core.service.customer;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.client.FileServiceClient;
import com.myshop.core.constant.CoreMessageKeys;
import com.myshop.core.constant.StorageBuckets;
import com.myshop.core.dto.file.PresignGetResponseDto;
import com.myshop.core.dto.file.PresignUploadResponseDto;
import com.myshop.core.dto.request.AvatarPresignRequest;
import com.myshop.core.dto.request.AvatarRequest;
import com.myshop.core.dto.request.ProfileRequest;
import com.myshop.core.dto.response.MediaPresignResponse;
import com.myshop.core.dto.response.ProfileResponse;
import com.myshop.core.entity.customer.UserProfile;
import com.myshop.core.repository.UserProfileRepository;
import com.myshop.core.util.ObjectKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FileServiceClient fileServiceClient;

    public ProfileResponse getByProfileId(Long profileId) {
        return toResponse(requireById(profileId));
    }

    public ProfileResponse getByUserId(Long userId) {
        return toResponse(requireByUserId(userId));
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
        UserProfile profile = requireById(profileId);
        if (request.getUsername() != null) profile.setUsername(request.getUsername());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getBirthDate() != null) profile.setBirthDate(request.getBirthDate());
        if (request.getMobileNumber() != null) profile.setMobileNumber(request.getMobileNumber());
        userProfileRepository.save(profile);
        return toResponse(profile);
    }

    public MediaPresignResponse presignAvatar(Long userId, AvatarPresignRequest request) {
        requireByUserId(userId);
        String objectKey = ObjectKeyUtils.avatarKey(userId, request.getFileName());
        ApiResponse<PresignUploadResponseDto> response = fileServiceClient.presignUpload(
                StorageBuckets.AVATARS,
                objectKey,
                request.getContentType()
        );
        PresignUploadResponseDto data = requireData(response);
        return MediaPresignResponse.builder()
                .bucket(StorageBuckets.AVATARS)
                .objectKey(data.getObjectKey() != null ? data.getObjectKey() : objectKey)
                .uploadUrl(data.getUploadUrl())
                .build();
    }

    @Transactional
    public ProfileResponse setAvatar(Long userId, AvatarRequest request) {
        if (!StringUtils.hasText(request.getObjectKey())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "objectKey is required");
        }
        UserProfile profile = requireByUserId(userId);
        if (StringUtils.hasText(profile.getAvatarObjectKey())) {
            fileServiceClient.delete(StorageBuckets.AVATARS, profile.getAvatarObjectKey());
        }
        profile.setAvatarObjectKey(request.getObjectKey());
        userProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse deleteAvatar(Long userId) {
        UserProfile profile = requireByUserId(userId);
        if (StringUtils.hasText(profile.getAvatarObjectKey())) {
            fileServiceClient.delete(StorageBuckets.AVATARS, profile.getAvatarObjectKey());
        }
        profile.setAvatarObjectKey(null);
        userProfileRepository.save(profile);
        return toResponse(profile);
    }

    private UserProfile requireById(Long profileId) {
        return userProfileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PROFILE_NOT_FOUND)
                ));
    }

    private UserProfile requireByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PROFILE_NOT_FOUND)
                ));
    }

    private ProfileResponse toResponse(UserProfile p) {
        String avatarUrl = null;
        if (StringUtils.hasText(p.getAvatarObjectKey())) {
            ApiResponse<PresignGetResponseDto> response =
                    fileServiceClient.presignGet(StorageBuckets.AVATARS, p.getAvatarObjectKey());
            PresignGetResponseDto data = response != null ? response.getData() : null;
            avatarUrl = data != null ? data.getUrl() : null;
        }
        return ProfileResponse.builder()
                .profileId(p.getProfileId())
                .userId(p.getUserId())
                .username(p.getUsername())
                .gender(p.getGender())
                .birthDate(p.getBirthDate())
                .mobileNumber(p.getMobileNumber())
                .avatarObjectKey(p.getAvatarObjectKey())
                .avatarUrl(avatarUrl)
                .build();
    }

    private static <T> T requireData(ApiResponse<T> response) {
        if (response == null || response.getData() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "file-service returned empty response");
        }
        return response.getData();
    }
}
