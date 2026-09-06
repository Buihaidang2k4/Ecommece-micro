package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaPresignResponse {
    private String bucket;
    private String objectKey;
    private String uploadUrl;
}
