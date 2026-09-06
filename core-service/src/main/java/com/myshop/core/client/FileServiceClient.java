package com.myshop.core.client;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.file.PresignGetResponseDto;
import com.myshop.core.dto.file.PresignUploadResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "file-service")
public interface FileServiceClient {

    @GetMapping(ApiPath.FILE_SERVICE_PRESIGN)
    ApiResponse<PresignGetResponseDto> presignGet(
            @RequestParam("bucket") String bucket,
            @RequestParam("objectKey") String objectKey);

    @PostMapping(ApiPath.FILE_SERVICE_PRESIGN_UPLOAD)
    ApiResponse<PresignUploadResponseDto> presignUpload(
            @RequestParam("bucket") String bucket,
            @RequestParam("objectKey") String objectKey,
            @RequestParam(value = "contentType", required = false) String contentType);

    @DeleteMapping(ApiPath.FILE_SERVICE)
    ApiResponse<Void> delete(
            @RequestParam("bucket") String bucket,
            @RequestParam("objectKey") String objectKey);
}
