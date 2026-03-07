package com.jway.blog.dto;

import java.util.List;

public record WebsiteSectionDto(String title, String icon, List<WebsiteLinkDto> links) {
}
