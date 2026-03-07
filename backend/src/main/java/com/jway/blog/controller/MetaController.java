package com.jway.blog.controller;

import com.jway.blog.common.ApiResponse;
import com.jway.blog.dto.CategoryCountDto;
import com.jway.blog.dto.TagCountDto;
import com.jway.blog.service.MetaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MetaController {

    private final MetaService metaService;

    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryCountDto>> categories() {
        return ApiResponse.success(metaService.categories());
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagCountDto>> tags() {
        return ApiResponse.success(metaService.tags());
    }
}
