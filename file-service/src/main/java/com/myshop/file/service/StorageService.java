package com.myshop.file.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minioClient;

    @Value("${minio.presign-expire}")
    private int presignExpireMinutes;

    public void upload(String bucket, String objectKey, MultipartFile file) throws Exception {
        ensureBucket(bucket);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
    }

    public void delete(String bucket, String objectKey) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .build());
    }

    public String presignGet(String bucket, String objectKey) throws Exception {
        ensureBucket(bucket);
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .method(Method.GET)
                .expiry(presignExpireMinutes, TimeUnit.MINUTES)
                .build());
    }

    public String presignPut(String bucket, String objectKey, String contentType) throws Exception {
        ensureBucket(bucket);
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .method(Method.PUT)
                .expiry(presignExpireMinutes, TimeUnit.MINUTES)
                .extraHeaders(java.util.Map.of("Content-Type", contentType == null ? "application/octet-stream" : contentType))
                .build());
    }

    private void ensureBucket(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }
}
