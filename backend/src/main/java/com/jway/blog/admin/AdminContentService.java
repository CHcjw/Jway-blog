package com.jway.blog.admin;

import com.jway.blog.admin.dto.*;
import com.jway.blog.dto.MomentDto;
import com.jway.blog.dto.WorkDto;
import com.jway.blog.entity.Moment;
import com.jway.blog.entity.WebsiteLink;
import com.jway.blog.entity.Work;
import com.jway.blog.mapper.BlogMapper;
import com.jway.blog.repository.MomentRepository;
import com.jway.blog.repository.WebsiteLinkRepository;
import com.jway.blog.repository.WorkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminContentService {

    private final WorkRepository workRepository;
    private final MomentRepository momentRepository;
    private final WebsiteLinkRepository websiteLinkRepository;

    public AdminContentService(WorkRepository workRepository,
                               MomentRepository momentRepository,
                               WebsiteLinkRepository websiteLinkRepository) {
        this.workRepository = workRepository;
        this.momentRepository = momentRepository;
        this.websiteLinkRepository = websiteLinkRepository;
    }

    public List<WorkDto> works() {
        return workRepository.findAll().stream()
            .map(BlogMapper::toWorkDto)
            .toList();
    }

    public WorkDto work(Long id) {
        Work work = workRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "work not found"));
        return BlogMapper.toWorkDto(work);
    }

    @Transactional
    public WorkDto createWork(AdminWorkUpsertRequest request) {
        Work work = new Work();
        apply(work, request);
        return BlogMapper.toWorkDto(workRepository.save(work));
    }

    @Transactional
    public WorkDto updateWork(Long id, AdminWorkUpsertRequest request) {
        Work work = workRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "work not found"));
        apply(work, request);
        return BlogMapper.toWorkDto(workRepository.save(work));
    }

    public void deleteWork(Long id) {
        if (!workRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "work not found");
        }
        workRepository.deleteById(id);
    }

    public List<MomentDto> moments() {
        return momentRepository.findAllByOrderByTimestampDesc().stream()
            .map(BlogMapper::toMomentDto)
            .toList();
    }

    public MomentDto moment(Long id) {
        Moment moment = momentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "moment not found"));
        return BlogMapper.toMomentDto(moment);
    }

    @Transactional
    public MomentDto createMoment(AdminMomentUpsertRequest request) {
        Moment moment = new Moment();
        apply(moment, request);
        return BlogMapper.toMomentDto(momentRepository.save(moment));
    }

    @Transactional
    public MomentDto updateMoment(Long id, AdminMomentUpsertRequest request) {
        Moment moment = momentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "moment not found"));
        apply(moment, request);
        return BlogMapper.toMomentDto(momentRepository.save(moment));
    }

    public void deleteMoment(Long id) {
        if (!momentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "moment not found");
        }
        momentRepository.deleteById(id);
    }

    public List<AdminWebsiteLinkDto> websiteLinks() {
        return websiteLinkRepository.findAllByOrderBySectionOrderAscSortOrderAscIdAsc().stream()
            .map(this::toAdminWebsiteLink)
            .toList();
    }

    public AdminWebsiteLinkDto websiteLink(Long id) {
        WebsiteLink link = websiteLinkRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "website link not found"));
        return toAdminWebsiteLink(link);
    }

    @Transactional
    public AdminWebsiteLinkDto createWebsiteLink(AdminWebsiteLinkUpsertRequest request) {
        WebsiteLink link = new WebsiteLink();
        apply(link, request);
        return toAdminWebsiteLink(websiteLinkRepository.save(link));
    }

    @Transactional
    public AdminWebsiteLinkDto updateWebsiteLink(Long id, AdminWebsiteLinkUpsertRequest request) {
        WebsiteLink link = websiteLinkRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "website link not found"));
        apply(link, request);
        return toAdminWebsiteLink(websiteLinkRepository.save(link));
    }

    public void deleteWebsiteLink(Long id) {
        if (!websiteLinkRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "website link not found");
        }
        websiteLinkRepository.deleteById(id);
    }

    private void apply(Work work, AdminWorkUpsertRequest req) {
        if (req == null
            || isBlank(req.title())
            || isBlank(req.image())
            || isBlank(req.desc())
            || isBlank(req.period())
            || isBlank(req.teamSize())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing required work fields");
        }
        work.setTitle(req.title().trim());
        work.setImageUrl(req.image().trim());
        work.setDescription(req.desc().trim());
        work.setPeriod(req.period().trim());
        work.setTeamSize(req.teamSize().trim());
        work.setTechs(joinPipe(req.techs()));
        work.setPoints(joinPipe(req.points()));
    }

    private void apply(Moment moment, AdminMomentUpsertRequest req) {
        if (req == null
            || isBlank(req.title())
            || isBlank(req.content())
            || isBlank(req.timestamp())
            || isBlank(req.type())
            || isBlank(req.size())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing required moment fields");
        }
        moment.setTitle(req.title().trim());
        moment.setContent(req.content().trim());
        moment.setTimestamp(LocalDate.parse(req.timestamp().trim()));
        moment.setType(req.type().trim());
        moment.setSize(req.size().trim());
    }

    private void apply(WebsiteLink link, AdminWebsiteLinkUpsertRequest req) {
        if (req == null
            || isBlank(req.sectionTitle())
            || isBlank(req.sectionIcon())
            || isBlank(req.name())
            || isBlank(req.desc())
            || isBlank(req.url())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing required website link fields");
        }
        link.setSectionTitle(req.sectionTitle().trim());
        link.setSectionIcon(req.sectionIcon().trim());
        link.setSectionOrder(req.sectionOrder() == null ? 0 : req.sectionOrder());
        link.setName(req.name().trim());
        link.setDescription(req.desc().trim());
        link.setUrl(req.url().trim());
        link.setLogoUrl(isBlank(req.logo()) ? null : req.logo().trim());
        link.setSortOrder(req.sortOrder() == null ? 0 : req.sortOrder());
    }

    private AdminWebsiteLinkDto toAdminWebsiteLink(WebsiteLink link) {
        return new AdminWebsiteLinkDto(
            link.getId(),
            link.getSectionTitle(),
            link.getSectionIcon(),
            link.getSectionOrder(),
            link.getName(),
            link.getDescription(),
            link.getUrl(),
            link.getLogoUrl(),
            link.getSortOrder()
        );
    }

    private String joinPipe(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
            .filter(v -> v != null && !v.isBlank())
            .map(String::trim)
            .collect(Collectors.joining("|"));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
