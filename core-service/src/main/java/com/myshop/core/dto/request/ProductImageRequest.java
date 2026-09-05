package com.myshop.core.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    private String fileName;
    private String fileType;
    private String objectKey;
    private String url;
    private String downloadUrl;
}
