package com.jway.blog.controller;

import com.jway.blog.config.OssProperties;
import com.jway.blog.service.OssMediaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final OssMediaService ossMediaService;
    private final OssProperties ossProperties;
    private final Map<String, RateWindow> requestWindows = new ConcurrentHashMap<>();

    public MediaController(OssMediaService ossMediaService, OssProperties ossProperties) {
        this.ossMediaService = ossMediaService;
        this.ossProperties = ossProperties;
    }

    @GetMapping("/object")
    public ResponseEntity<InputStreamResource> object(
        @RequestParam String key,
        HttpServletRequest request
    ) {
        if (!allowAccess(clientIp(request))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "rate limited");
        }
        OssMediaService.OssObjectStream stream = ossMediaService.getObjectStream(key);
        MediaType mediaType = resolveContentType(stream.contentType());
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(Math.max(stream.contentLength(), 0)))
            .body(new InputStreamResource(stream.body()));
    }

    private boolean allowAccess(String ip) {
        long now = System.currentTimeMillis();
        long currentWindow = now - (now % 60_000L);
        int maxPerMinute = Math.max(1, ossProperties.mediaRateLimitPerMinute());

        RateWindow window = requestWindows.compute(ip, (k, existing) -> {
            if (existing == null || existing.windowStart() != currentWindow) {
                return new RateWindow(currentWindow, new AtomicInteger(1));
            }
            existing.count().incrementAndGet();
            return existing;
        });
        return window.count().get() <= maxPerMinute;
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private MediaType resolveContentType(String value) {
        if (value == null || value.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(value);
        } catch (Exception ignored) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private record RateWindow(long windowStart, AtomicInteger count) {
    }
}
