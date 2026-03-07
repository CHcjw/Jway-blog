package com.jway.blog.controller;

import com.jway.blog.common.ApiResponse;
import com.jway.blog.dto.PostDetailDto;
import com.jway.blog.dto.PostPageDto;
import com.jway.blog.dto.PostSummaryDto;
import com.jway.blog.service.PostService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<PostPageDto> page(
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success(postService.getPosts(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDetailDto> detail(@PathVariable Long id) {
        return ApiResponse.success(postService.getPostDetail(id));
    }

    @GetMapping("/latest")
    public ApiResponse<List<PostSummaryDto>> latest(@RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit) {
        return ApiResponse.success(postService.getLatest(limit));
    }

    @GetMapping("/related/{id}")
    public ApiResponse<List<PostSummaryDto>> related(
        @PathVariable Long id,
        @RequestParam(defaultValue = "4") @Min(1) @Max(20) int limit
    ) {
        return ApiResponse.success(postService.getRelated(id, limit));
    }

    @GetMapping("/search")
    public ApiResponse<PostPageDto> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String tag,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "9") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success(postService.search(keyword, category, tag, page, size));
    }
}
