package com.jway.blog.admin.dto;

public record AdminMomentUpsertRequest(
    String title,
    String content,
    String timestamp,
    String type,
    String size
) {
}
