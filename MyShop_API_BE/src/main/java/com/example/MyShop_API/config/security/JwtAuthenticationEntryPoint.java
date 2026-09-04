package com.example.MyShop_API.config.security;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

//Xử lý lỗi khi chưa đăng nhập (401)
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //  Convert khi callapi sai không trả về login mà trả về JSON tùy chỉnh Custom
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("Unauthorized access attempt: {}", authException.getMessage());
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        // set
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // tao apiresponse set code status
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessageTemplate())
                .build();

        // Convert sang JSON
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writer().writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
