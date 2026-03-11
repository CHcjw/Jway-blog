package com.jway.blog.admin;

import com.jway.blog.admin.dto.AdminPostUpsertRequest;
import com.jway.blog.common.MarkdownTocUtil;
import com.jway.blog.dto.PostDetailDto;
import com.jway.blog.dto.PostPageDto;
import com.jway.blog.dto.PostSummaryDto;
import com.jway.blog.entity.Category;
import com.jway.blog.entity.Post;
import com.jway.blog.entity.Tag;
import com.jway.blog.mapper.BlogMapper;
import com.jway.blog.repository.CategoryRepository;
import com.jway.blog.repository.PostRepository;
import com.jway.blog.repository.TagRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminPostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public AdminPostService(PostRepository postRepository,
                            CategoryRepository categoryRepository,
                            TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public PostPageDto page(int page, int size) {
        var p = postRepository.findAll(
            PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "publishDate"))
        );
        List<PostSummaryDto> list = p.getContent().stream().map(BlogMapper::toPostSummary).toList();
        return new PostPageDto(p.getTotalElements(), list);
    }

    public PostDetailDto detail(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        return BlogMapper.toPostDetail(post);
    }

    @Transactional
    public PostDetailDto create(AdminPostUpsertRequest request) {
        Post post = new Post();
        apply(post, request);
        post.setViews(0);
        return BlogMapper.toPostDetail(postRepository.save(post));
    }

    @Transactional
    public PostDetailDto update(Long id, AdminPostUpsertRequest request) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found"));
        apply(post, request);
        return BlogMapper.toPostDetail(postRepository.save(post));
    }

    public void delete(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found");
        }
        postRepository.deleteById(id);
    }

    private void apply(Post post, AdminPostUpsertRequest req) {
        if (req == null || isBlank(req.title()) || isBlank(req.summary()) || isBlank(req.content())
            || isBlank(req.cover()) || isBlank(req.date()) || isBlank(req.category())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing required fields");
        }

        post.setTitle(req.title().trim());
        post.setSummary(req.summary().trim());
        post.setContent(MarkdownTocUtil.upsertToc(req.content()));
        post.setCoverUrl(req.cover().trim());
        post.setPublishDate(LocalDate.parse(req.date().trim()));
        post.setTop(Boolean.TRUE.equals(req.isTop()));

        Category category = categoryRepository.findByName(req.category().trim())
            .orElseGet(() -> {
                Category c = new Category();
                c.setName(req.category().trim());
                c.setCreateTime(LocalDateTime.now());
                return categoryRepository.save(c);
            });
        post.setCategory(category);

        Set<Tag> tags = new HashSet<>();
        if (req.tags() != null) {
            for (String raw : req.tags()) {
                if (isBlank(raw)) continue;
                String name = raw.trim();
                Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        t.setName(name);
                        t.setCreateTime(LocalDateTime.now());
                        return tagRepository.save(t);
                    });
                tags.add(tag);
            }
        }
        post.setTags(tags);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
