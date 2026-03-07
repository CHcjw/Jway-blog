package com.jway.blog.mapper;

import com.jway.blog.dto.*;
import com.jway.blog.entity.Moment;
import com.jway.blog.entity.Post;
import com.jway.blog.entity.Skill;
import com.jway.blog.entity.WebsiteLink;
import com.jway.blog.entity.Work;

import java.util.Arrays;
import java.util.List;

public final class BlogMapper {

    private BlogMapper() {
    }

    public static PostSummaryDto toPostSummary(Post post) {
        return new PostSummaryDto(
            post.getId(),
            post.getTitle(),
            post.getSummary(),
            post.getCoverUrl(),
            post.getPublishDate().toString(),
            post.getCategory().getName(),
            post.getTags().stream().map(t -> t.getName()).sorted().toList(),
            post.isTop()
        );
    }

    public static PostDetailDto toPostDetail(Post post) {
        return new PostDetailDto(
            post.getId(),
            post.getTitle(),
            post.getSummary(),
            post.getContent(),
            post.getCoverUrl(),
            post.getPublishDate().toString(),
            post.getCategory().getName(),
            post.getTags().stream().map(t -> t.getName()).sorted().toList(),
            post.getViews(),
            post.isTop()
        );
    }

    public static WorkDto toWorkDto(Work work) {
        return new WorkDto(
            work.getId(),
            work.getTitle(),
            work.getImageUrl(),
            work.getDescription(),
            work.getPeriod(),
            work.getTeamSize(),
            splitByPipe(work.getTechs()),
            splitByPipe(work.getPoints())
        );
    }

    public static SkillDto toSkillDto(Skill skill) {
        return new SkillDto(skill.getId(), skill.getName(), skill.getIcon(), skill.getColor());
    }

    public static MomentDto toMomentDto(Moment moment) {
        return new MomentDto(
            moment.getId(),
            moment.getTitle(),
            moment.getContent(),
            moment.getTimestamp().toString(),
            moment.getType(),
            moment.getSize()
        );
    }

    public static WebsiteLinkDto toWebsiteLinkDto(WebsiteLink link) {
        return new WebsiteLinkDto(
            link.getId(),
            link.getName(),
            link.getDescription(),
            link.getUrl(),
            link.getLogoUrl()
        );
    }

    private static List<String> splitByPipe(String value) {
        return Arrays.stream(value.split("\\|"))
            .map(String::trim)
            .filter(s -> !s.isBlank())
            .toList();
    }
}
