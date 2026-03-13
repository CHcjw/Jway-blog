package com.jway.blog.service;

import com.jway.blog.dto.PostPageDto;
import com.jway.blog.entity.Category;
import com.jway.blog.entity.Post;
import com.jway.blog.entity.Tag;
import com.jway.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldPlaceTopPostsBeforeNewerNonTopPosts() {
        Post topButOlder = buildPost(1L, true, LocalDate.parse("2025-01-01"), "Top Older");
        Post nonTopButNewer = buildPost(2L, false, LocalDate.parse("2026-12-31"), "Non Top Newer");

        when(postRepository.findAll()).thenReturn(List.of(nonTopButNewer, topButOlder));

        PostPageDto result = postService.getPosts(1, 10);

        assertTrue(result.list().get(0).isTop(), "Top post should be first even if date is older");
        assertFalse(result.list().get(1).isTop(), "Non-top post should come after top posts");
    }

    private Post buildPost(Long id, boolean isTop, LocalDate date, String title) {
        Category category = new Category();
        category.setName("Test Category");

        Tag tag = new Tag();
        tag.setName("test-tag");

        Post post = new Post();
        post.setId(id);
        post.setTitle(title);
        post.setSummary("summary");
        post.setContent("content");
        post.setCoverUrl("https://example.com/cover.jpg");
        post.setPublishDate(date);
        post.setViews(0);
        post.setTop(isTop);
        post.setCategory(category);
        post.setTags(Set.of(tag));
        return post;
    }
}
