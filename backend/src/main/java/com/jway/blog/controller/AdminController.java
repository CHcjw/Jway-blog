package com.jway.blog.controller;

import com.jway.blog.admin.AdminAuthService;
import com.jway.blog.admin.AdminContentService;
import com.jway.blog.admin.AdminPostService;
import com.jway.blog.admin.dto.*;
import com.jway.blog.common.ApiResponse;
import com.jway.blog.dto.MomentDto;
import com.jway.blog.dto.PostDetailDto;
import com.jway.blog.dto.PostPageDto;
import com.jway.blog.dto.WorkDto;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Hidden
@RestController
@RequestMapping("${admin.api-prefix}")
public class AdminController {

    private final AdminAuthService adminAuthService;
    private final AdminPostService adminPostService;
    private final AdminContentService adminContentService;

    public AdminController(AdminAuthService adminAuthService,
                           AdminPostService adminPostService,
                           AdminContentService adminContentService) {
        this.adminAuthService = adminAuthService;
        this.adminPostService = adminPostService;
        this.adminContentService = adminContentService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminLoginDto> login(@RequestBody AdminLoginRequest request,
                                            @RequestHeader("X-Admin-Entry-Key") String entryKey) {
        return ApiResponse.success(adminAuthService.login(request.username(), request.password(), entryKey));
    }

    @GetMapping("/posts")
    public ApiResponse<PostPageDto> posts(
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return ApiResponse.success(adminPostService.page(page, size));
    }

    @GetMapping("/posts/{id}")
    public ApiResponse<PostDetailDto> post(@PathVariable Long id) {
        return ApiResponse.success(adminPostService.detail(id));
    }

    @PostMapping("/posts")
    public ApiResponse<PostDetailDto> create(@RequestBody AdminPostUpsertRequest request) {
        return ApiResponse.success(adminPostService.create(request));
    }

    @PutMapping("/posts/{id}")
    public ApiResponse<PostDetailDto> update(@PathVariable Long id, @RequestBody AdminPostUpsertRequest request) {
        return ApiResponse.success(adminPostService.update(id, request));
    }

    @DeleteMapping("/posts/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        adminPostService.delete(id);
        return ApiResponse.success(true);
    }

    @GetMapping("/works")
    public ApiResponse<List<WorkDto>> works() {
        return ApiResponse.success(adminContentService.works());
    }

    @GetMapping("/works/{id}")
    public ApiResponse<WorkDto> work(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.work(id));
    }

    @PostMapping("/works")
    public ApiResponse<WorkDto> createWork(@RequestBody AdminWorkUpsertRequest request) {
        return ApiResponse.success(adminContentService.createWork(request));
    }

    @PutMapping("/works/{id}")
    public ApiResponse<WorkDto> updateWork(@PathVariable Long id, @RequestBody AdminWorkUpsertRequest request) {
        return ApiResponse.success(adminContentService.updateWork(id, request));
    }

    @DeleteMapping("/works/{id}")
    public ApiResponse<Boolean> deleteWork(@PathVariable Long id) {
        adminContentService.deleteWork(id);
        return ApiResponse.success(true);
    }

    @GetMapping("/moments")
    public ApiResponse<List<MomentDto>> moments() {
        return ApiResponse.success(adminContentService.moments());
    }

    @GetMapping("/moments/{id}")
    public ApiResponse<MomentDto> moment(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.moment(id));
    }

    @PostMapping("/moments")
    public ApiResponse<MomentDto> createMoment(@RequestBody AdminMomentUpsertRequest request) {
        return ApiResponse.success(adminContentService.createMoment(request));
    }

    @PutMapping("/moments/{id}")
    public ApiResponse<MomentDto> updateMoment(@PathVariable Long id, @RequestBody AdminMomentUpsertRequest request) {
        return ApiResponse.success(adminContentService.updateMoment(id, request));
    }

    @DeleteMapping("/moments/{id}")
    public ApiResponse<Boolean> deleteMoment(@PathVariable Long id) {
        adminContentService.deleteMoment(id);
        return ApiResponse.success(true);
    }

    @GetMapping("/website-links")
    public ApiResponse<List<AdminWebsiteLinkDto>> websiteLinks() {
        return ApiResponse.success(adminContentService.websiteLinks());
    }

    @GetMapping("/website-links/{id}")
    public ApiResponse<AdminWebsiteLinkDto> websiteLink(@PathVariable Long id) {
        return ApiResponse.success(adminContentService.websiteLink(id));
    }

    @PostMapping("/website-links")
    public ApiResponse<AdminWebsiteLinkDto> createWebsiteLink(@RequestBody AdminWebsiteLinkUpsertRequest request) {
        return ApiResponse.success(adminContentService.createWebsiteLink(request));
    }

    @PutMapping("/website-links/{id}")
    public ApiResponse<AdminWebsiteLinkDto> updateWebsiteLink(@PathVariable Long id,
                                                               @RequestBody AdminWebsiteLinkUpsertRequest request) {
        return ApiResponse.success(adminContentService.updateWebsiteLink(id, request));
    }

    @DeleteMapping("/website-links/{id}")
    public ApiResponse<Boolean> deleteWebsiteLink(@PathVariable Long id) {
        adminContentService.deleteWebsiteLink(id);
        return ApiResponse.success(true);
    }
}
