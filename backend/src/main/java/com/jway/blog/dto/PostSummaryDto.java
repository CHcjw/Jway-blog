package com.jway.blog.dto;

import java.util.List;

public record PostSummaryDto(
    Long id,
    String title,
    String summary,
    String cover,
    String date,
    String category,
    List<String> tags,
    boolean isTop
) {
}
