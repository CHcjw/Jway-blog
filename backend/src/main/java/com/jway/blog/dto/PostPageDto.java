package com.jway.blog.dto;

import java.util.List;

public record PostPageDto(long total, List<PostSummaryDto> list) {
}
