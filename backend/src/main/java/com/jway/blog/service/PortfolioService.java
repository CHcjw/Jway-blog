package com.jway.blog.service;

import com.jway.blog.dto.SkillsGroupDto;
import com.jway.blog.dto.WorkDto;
import com.jway.blog.mapper.BlogMapper;
import com.jway.blog.repository.SkillRepository;
import com.jway.blog.repository.WorkRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PortfolioService {

    private final WorkRepository workRepository;
    private final SkillRepository skillRepository;

    public PortfolioService(WorkRepository workRepository, SkillRepository skillRepository) {
        this.workRepository = workRepository;
        this.skillRepository = skillRepository;
    }

    public List<WorkDto> works() {
        return workRepository.findAll().stream()
            .sorted(Comparator.comparing(work -> work.getId()))
            .map(BlogMapper::toWorkDto)
            .toList();
    }

    public SkillsGroupDto skills() {
        return new SkillsGroupDto(
            skillRepository.findAllByTypeOrderByNameAsc(1).stream().map(BlogMapper::toSkillDto).toList(),
            skillRepository.findAllByTypeOrderByNameAsc(2).stream().map(BlogMapper::toSkillDto).toList()
        );
    }
}
