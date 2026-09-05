package com.myshop.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse implements Serializable {
    private Long id;
    private String fileName;
    private String fileType;
    private String objectKey;
    private String url;
    private String downloadUrl;
}
