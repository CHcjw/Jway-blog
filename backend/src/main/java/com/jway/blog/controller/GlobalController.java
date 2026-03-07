package com.jway.blog.controller;

import com.jway.blog.common.ApiResponse;
import com.jway.blog.dto.MomentDto;
import com.jway.blog.dto.StatisticsDto;
import com.jway.blog.dto.TrackVisitDto;
import com.jway.blog.dto.TrackVisitRequest;
import com.jway.blog.dto.WebsiteSectionDto;
import com.jway.blog.service.GlobalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GlobalController {

    private final GlobalService globalService;

    public GlobalController(GlobalService globalService) {
        this.globalService = globalService;
    }

    @GetMapping("/moments")
    public ApiResponse<List<MomentDto>> moments() {
        return ApiResponse.success(globalService.moments());
    }

    @GetMapping("/website-links")
    public ApiResponse<List<WebsiteSectionDto>> websiteLinks() {
        return ApiResponse.success(globalService.websiteLinks());
    }

    @GetMapping("/statistics")
    public ApiResponse<StatisticsDto> statistics() {
        return ApiResponse.success(globalService.statistics());
    }

    @PostMapping("/track/visit")
    public ApiResponse<TrackVisitDto> trackVisit(@RequestBody(required = false) TrackVisitRequest request) {
        String visitorId = request == null ? null : request.visitorId();
        String path = request == null ? null : request.path();
        return ApiResponse.success(globalService.trackVisit(visitorId, path));
    }
}
