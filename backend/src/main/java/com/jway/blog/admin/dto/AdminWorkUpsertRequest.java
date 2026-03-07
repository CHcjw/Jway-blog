package com.jway.blog.admin.dto;

import java.util.List;

public record AdminWorkUpsertRequest(
    String title,
    String image,
    String desc,
    String period,
    String teamSize,
    List<String> techs,
    List<String> points
) {
}
