package com.jway.blog.dto;

import java.util.List;

public record PostDetailDto(
    Long id,
    String title,
    String summary,
    String content,
    String cover,
    String date,
    String category,
    List<String> tags,
    int views,
    boolean isTop
) {
}
