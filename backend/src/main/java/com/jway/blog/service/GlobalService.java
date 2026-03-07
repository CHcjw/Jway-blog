package com.jway.blog.service;

import com.jway.blog.dto.MomentDto;
import com.jway.blog.dto.StatisticsDto;
import com.jway.blog.dto.TrackVisitDto;
import com.jway.blog.dto.WebsiteSectionDto;
import com.jway.blog.mapper.BlogMapper;
import com.jway.blog.entity.VisitEvent;
import com.jway.blog.entity.Visitor;
import com.jway.blog.repository.MomentRepository;
import com.jway.blog.repository.PostRepository;
import com.jway.blog.repository.VisitEventRepository;
import com.jway.blog.repository.VisitorRepository;
import com.jway.blog.repository.WebsiteLinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GlobalService {

    private final MomentRepository momentRepository;
    private final PostRepository postRepository;
    private final VisitorRepository visitorRepository;
    private final VisitEventRepository visitEventRepository;
    private final WebsiteLinkRepository websiteLinkRepository;

    public GlobalService(MomentRepository momentRepository,
                         PostRepository postRepository,
                         VisitorRepository visitorRepository,
                         VisitEventRepository visitEventRepository,
                         WebsiteLinkRepository websiteLinkRepository) {
        this.momentRepository = momentRepository;
        this.postRepository = postRepository;
        this.visitorRepository = visitorRepository;
        this.visitEventRepository = visitEventRepository;
        this.websiteLinkRepository = websiteLinkRepository;
    }

    public List<MomentDto> moments() {
        return momentRepository.findAllByOrderByTimestampDesc().stream()
            .map(BlogMapper::toMomentDto)
            .toList();
    }

    public StatisticsDto statistics() {
        long totalPosts = postRepository.count();
        long totalViews = visitEventRepository.count();
        long totalWords = postRepository.findAll().stream()
            .map(p -> p.getContent().replaceAll("\\s+", ""))
            .mapToLong(String::length)
            .sum();
        long totalVisitors = visitorRepository.count();
        return new StatisticsDto(totalPosts, totalWords, totalViews, totalVisitors);
    }

    public List<WebsiteSectionDto> websiteLinks() {
        Map<String, WebsiteSectionDto> grouped = new LinkedHashMap<>();
        websiteLinkRepository.findAllByOrderBySectionOrderAscSortOrderAscIdAsc().forEach(link -> {
            String key = link.getSectionTitle() + "||" + link.getSectionIcon();
            WebsiteSectionDto section = grouped.computeIfAbsent(
                key,
                ignored -> new WebsiteSectionDto(link.getSectionTitle(), link.getSectionIcon(), new java.util.ArrayList<>())
            );
            section.links().add(BlogMapper.toWebsiteLinkDto(link));
        });
        return List.copyOf(grouped.values());
    }

    public TrackVisitDto trackVisit(String visitorId, String path) {
        LocalDateTime now = LocalDateTime.now();
        String normalizedId = visitorId == null || visitorId.isBlank() ? null : visitorId.trim();
        Visitor visitor = normalizedId == null
            ? null
            : visitorRepository.findByVisitorKey(normalizedId).orElse(null);

        if (visitor == null) {
            visitor = new Visitor();
            visitor.setVisitorKey(UUID.randomUUID().toString().replace("-", ""));
            visitor.setFirstVisitAt(now);
        }
        visitor.setLastVisitAt(now);
        visitor = visitorRepository.save(visitor);

        VisitEvent event = new VisitEvent();
        event.setVisitor(visitor);
        event.setPath(path == null || path.isBlank() ? "/" : path.trim());
        event.setVisitedAt(now);
        visitEventRepository.save(event);

        return new TrackVisitDto(visitor.getVisitorKey(), visitorRepository.count(), visitEventRepository.count());
    }
}
