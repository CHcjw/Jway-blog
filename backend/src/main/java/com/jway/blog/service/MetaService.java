package com.jway.blog.service;

import com.jway.blog.dto.CategoryCountDto;
import com.jway.blog.dto.TagCountDto;
import com.jway.blog.entity.Post;
import com.jway.blog.repository.CategoryRepository;
import com.jway.blog.repository.PostRepository;
import com.jway.blog.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public MetaService(CategoryRepository categoryRepository, TagRepository tagRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public List<CategoryCountDto> categories() {
        List<Post> posts = postRepository.findAll();
        return categoryRepository.findAll().stream()
            .map(c -> new CategoryCountDto(c.getName(), posts.stream().filter(p -> p.getCategory().getName().equals(c.getName())).count()))
            .sorted(java.util.Comparator.comparing(CategoryCountDto::count).reversed())
            .toList();
    }

    public List<TagCountDto> tags() {
        List<Post> posts = postRepository.findAll();
        return tagRepository.findAll().stream()
            .map(t -> new TagCountDto(t.getName(), posts.stream().filter(p -> p.getTags().stream().anyMatch(v -> v.getName().equals(t.getName()))).count()))
            .sorted(java.util.Comparator.comparing(TagCountDto::count).reversed())
            .toList();
    }
}
