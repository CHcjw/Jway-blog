package com.jway.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oss")
public record OssProperties(
    String endpoint,
    String bucket,
    String accessKeyId,
    String accessKeySecret,
    String publicHost,
    int mediaRateLimitPerMinute
) {
}

