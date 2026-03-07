package com.jway.blog.dto;

import java.util.List;

public record SkillsGroupDto(List<SkillDto> frontend, List<SkillDto> backend) {
}
