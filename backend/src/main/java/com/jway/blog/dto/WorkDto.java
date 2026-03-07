package com.jway.blog.dto;

import java.util.List;

public record WorkDto(
    Long id,
    String title,
    String image,
    String desc,
    String period,
    String teamSize,
    List<String> techs,
    List<String> points
) {
}
