package com.myshop.notification.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.notification.constant.ApiPath;
import com.myshop.notification.document.NotificationLog;
import com.myshop.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPath.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationLogRepository repository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationLog>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(repository.findAll()));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<NotificationLog>> byEventId(@PathVariable String eventId) {
        return repository.findByEventId(eventId)
                .map(n -> ResponseEntity.ok(ApiResponse.ok(n)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
