package com.myshop.core.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignUploadResponseDto {
    private String objectKey;
    private String uploadUrl;
}
