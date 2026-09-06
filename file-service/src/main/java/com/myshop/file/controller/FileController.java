package com.myshop.file.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.file.constant.ApiPath;
import com.myshop.file.dto.response.FileUploadResponse;
import com.myshop.file.dto.response.PresignGetResponse;
import com.myshop.file.dto.response.PresignUploadResponse;
import com.myshop.file.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(ApiPath.FILES)
@RequiredArgsConstructor
public class FileController {
    private final StorageService storageService;

    @PostMapping(ApiPath.UPLOAD)
    public ResponseEntity<ApiResponse<FileUploadResponse>> upload(
            @RequestParam String bucket,
            @RequestParam String objectKey,
            @RequestParam("file") MultipartFile file) throws Exception {
        storageService.upload(bucket, objectKey, file);
        return ResponseEntity.ok(ApiResponse.ok(FileUploadResponse.builder()
                .objectKey(objectKey)
                .build()));
    }

    @PostMapping(ApiPath.PRESIGN_UPLOAD)
    public ResponseEntity<ApiResponse<PresignUploadResponse>> presignUpload(
            @RequestParam String bucket,
            @RequestParam String objectKey,
            @RequestParam(required = false) String contentType) throws Exception {
        String url = storageService.presignPut(bucket, objectKey, contentType);
        return ResponseEntity.ok(ApiResponse.ok(PresignUploadResponse.builder()
                .objectKey(objectKey)
                .uploadUrl(url)
                .build()));
    }

    @GetMapping(ApiPath.PRESIGN)
    public ResponseEntity<ApiResponse<PresignGetResponse>> presignGet(
            @RequestParam String bucket,
            @RequestParam String objectKey) throws Exception {
        String url = storageService.presignGet(bucket, objectKey);
        return ResponseEntity.ok(ApiResponse.ok(PresignGetResponse.builder()
                .url(url)
                .build()));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestParam String bucket,
            @RequestParam String objectKey) throws Exception {
        storageService.delete(bucket, objectKey);
        return ResponseEntity.ok(ApiResponse.of(200, "Deleted", null));
    }
}
