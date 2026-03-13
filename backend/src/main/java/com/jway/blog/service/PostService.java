package com.jway.blog.service;

import com.jway.blog.dto.PostDetailDto;
import com.jway.blog.dto.PostPageDto;
import com.jway.blog.dto.PostSummaryDto;
import com.jway.blog.entity.Post;
import com.jway.blog.mapper.BlogMapper;
import com.jway.blog.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostPageDto getPosts(int page, int size) {
        List<Post> ordered = sortByTopThenDateDesc(postRepository.findAll());
        return paginateAndMap(ordered, page, size);
    }

    public PostDetailDto getPostDetail(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
        return BlogMapper.toPostDetail(post);
    }

    public List<PostSummaryDto> getLatest(int limit) {
        return sortByTopThenDateDesc(postRepository.findAll()).stream()
            .limit(limit)
            .map(BlogMapper::toPostSummary)
            .toList();
    }

    public List<PostSummaryDto> getRelated(Long id, int limit) {
        Post current = postRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        Set<String> currentTags = current.getTags().stream().map(t -> t.getName()).collect(java.util.stream.Collectors.toSet());
        String category = current.getCategory().getName();

        return postRepository.findAll().stream()
            .filter(p -> !p.getId().equals(id))
            .sorted(Comparator
                .comparingInt((Post p) -> relatedScore(p, currentTags, category)).reversed()
                .thenComparing(Post::getPublishDate, Comparator.reverseOrder()))
            .limit(limit)
            .map(BlogMapper::toPostSummary)
            .toList();
    }

    public PostPageDto search(String keyword, String category, String tag, int page, int size) {
        String q = normalize(keyword);
        String c = normalize(category);
        String t = normalize(tag);

        List<Post> filtered = sortByTopThenDateDesc(postRepository.findAll()).stream()
            .filter(p -> q == null
                || p.getTitle().toLowerCase(Locale.ROOT).contains(q)
                || p.getCategory().getName().toLowerCase(Locale.ROOT).contains(q))
            .filter(p -> c == null || p.getCategory().getName().equalsIgnoreCase(c))
            .filter(p -> t == null || p.getTags().stream().anyMatch(v -> v.getName().equalsIgnoreCase(t)))
            .toList();

        return paginateAndMap(filtered, page, size);
    }

    private PostPageDto paginateAndMap(List<Post> posts, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int start = (safePage - 1) * safeSize;
        if (start >= posts.size()) {
            return new PostPageDto(posts.size(), List.of());
        }
        int end = Math.min(start + safeSize, posts.size());
        return new PostPageDto(posts.size(), posts.subList(start, end).stream().map(BlogMapper::toPostSummary).toList());
    }

    private List<Post> sortByTopThenDateDesc(List<Post> posts) {
        return posts.stream()
            .sorted(Comparator
                .comparing(Post::isTop, Comparator.reverseOrder())
                .thenComparing(Post::getPublishDate, Comparator.reverseOrder()))
            .toList();
    }

    private int relatedScore(Post candidate, Set<String> currentTags, String category) {
        int score = candidate.getCategory().getName().equals(category) ? 2 : 0;
        int tagHits = (int) candidate.getTags().stream().map(t -> t.getName()).filter(currentTags::contains).count();
        return score + tagHits;
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
