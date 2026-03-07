package com.jway.blog.controller;

import com.jway.blog.common.ApiResponse;
import com.jway.blog.dto.SkillsGroupDto;
import com.jway.blog.dto.WorkDto;
import com.jway.blog.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/works")
    public ApiResponse<List<WorkDto>> works() {
        return ApiResponse.success(portfolioService.works());
    }

    @GetMapping("/skills")
    public ApiResponse<SkillsGroupDto> skills() {
        return ApiResponse.success(portfolioService.skills());
    }
}
