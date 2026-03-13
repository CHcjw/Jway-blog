package com.jway.blog.service;

import com.jway.blog.dto.CategoryCountDto;
import com.jway.blog.entity.Category;
import com.jway.blog.entity.Post;
import com.jway.blog.repository.CategoryRepository;
import com.jway.blog.repository.PostRepository;
import com.jway.blog.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetaServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private MetaService metaService;

    @Test
    void shouldExcludeCategoriesWithoutPosts() {
        Category java = new Category();
        java.setName("Java");

        Category empty = new Category();
        empty.setName("Empty");

        Post javaPost = new Post();
        javaPost.setTitle("Java Post");
        javaPost.setPublishDate(LocalDate.parse("2026-01-01"));
        javaPost.setCategory(java);

        when(categoryRepository.findAll()).thenReturn(List.of(java, empty));
        when(postRepository.findAll()).thenReturn(List.of(javaPost));

        List<CategoryCountDto> categories = metaService.categories();

        assertEquals(1, categories.size());
        assertEquals("Java", categories.get(0).name());
        assertEquals(1L, categories.get(0).count());
    }
}
