package com.myshop.file.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.file.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String objectKey = storageService.upload(file);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("objectKey", objectKey)));
    }

    @PostMapping("/presign-upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> presignUpload(
            @RequestParam String fileName,
            @RequestParam(required = false) String contentType) throws Exception {
        String objectKey = UUID.randomUUID() + "-" + fileName;
        String url = storageService.presignPut(objectKey, contentType);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("objectKey", objectKey, "uploadUrl", url)));
    }

    @GetMapping("/presign")
    public ResponseEntity<ApiResponse<Map<String, String>>> presignGet(@RequestParam String objectKey) throws Exception {
        String url = storageService.presignGet(objectKey);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam String objectKey) throws Exception {
        storageService.delete(objectKey);
        return ResponseEntity.ok(ApiResponse.of(200, "Deleted", null));
    }
}
