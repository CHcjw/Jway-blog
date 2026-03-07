package com.jway.blog.admin.dto;

public record AdminWebsiteLinkDto(
    Long id,
    String sectionTitle,
    String sectionIcon,
    Integer sectionOrder,
    String name,
    String desc,
    String url,
    String logo,
    Integer sortOrder
) {
}
