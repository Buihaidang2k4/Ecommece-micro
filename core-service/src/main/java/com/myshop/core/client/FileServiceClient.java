package com.myshop.core.client;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "file-service")
public interface FileServiceClient {

    @GetMapping(ApiPath.FILE_SERVICE_PRESIGN)
    ApiResponse<Map<String, String>> presignGet(@RequestParam("objectKey") String objectKey);
}
