package com.jway.blog.admin.dto;

import java.util.List;

public record AdminPostUpsertRequest(
    String title,
    String summary,
    String content,
    String cover,
    String date,
    String category,
    List<String> tags,
    Boolean isTop
) {
}
