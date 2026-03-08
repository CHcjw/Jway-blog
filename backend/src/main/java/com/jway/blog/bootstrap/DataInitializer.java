package com.jway.blog.bootstrap;

import com.jway.blog.entity.Category;
import com.jway.blog.entity.Moment;
import com.jway.blog.entity.Post;
import com.jway.blog.entity.Skill;
import com.jway.blog.entity.Tag;
import com.jway.blog.entity.WebsiteLink;
import com.jway.blog.entity.Work;
import com.jway.blog.repository.CategoryRepository;
import com.jway.blog.repository.MomentRepository;
import com.jway.blog.repository.PostRepository;
import com.jway.blog.repository.SkillRepository;
import com.jway.blog.repository.TagRepository;
import com.jway.blog.repository.WebsiteLinkRepository;
import com.jway.blog.repository.WorkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final WorkRepository workRepository;
    private final SkillRepository skillRepository;
    private final MomentRepository momentRepository;
    private final WebsiteLinkRepository websiteLinkRepository;

    public DataInitializer(PostRepository postRepository,
                           CategoryRepository categoryRepository,
                           TagRepository tagRepository,
                           WorkRepository workRepository,
                           SkillRepository skillRepository,
                           MomentRepository momentRepository,
                           WebsiteLinkRepository websiteLinkRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.workRepository = workRepository;
        this.skillRepository = skillRepository;
        this.momentRepository = momentRepository;
        this.websiteLinkRepository = websiteLinkRepository;
    }

    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            seedPosts();
        }
        if (workRepository.count() == 0) {
            seedWorks();
        }
        if (skillRepository.count() == 0) {
            seedSkills();
        }
        if (momentRepository.count() == 0) {
            seedMoments();
        }
        if (websiteLinkRepository.count() == 0) {
            seedWebsiteLinks();
        }
    }

    private void seedPosts() {
        Map<String, Category> categories = new HashMap<>();
        for (String name : List.of("学习笔记", "技术分享", "后端", "面试", "算法", "生活")) {
            Category c = new Category();
            c.setName(name);
            c.setCreateTime(LocalDateTime.now());
            categories.put(name, categoryRepository.save(c));
        }

        Map<String, Tag> tags = new HashMap<>();
        for (String name : List.of(
            "Java", "Vue3", "Vite", "Redis", "缓存", "SpringCloud", "算法", "刷题",
            "性能", "前端", "JUC", "多线程", "Docker", "DevOps", "Gateway", "微服务",
            "Kafka", "MQ", "MySQL", "数据库", "生活", "总结", "实习")) {
            Tag t = new Tag();
            t.setName(name);
            t.setCreateTime(LocalDateTime.now());
            tags.put(name, tagRepository.save(t));
        }

        createPost("Java 学习路线与实习心得", "从零基础到进阶的实践路径", "学习笔记", List.of("Java", "实习"), LocalDate.parse("2026-02-04"), true,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-1.jpg", "# Java 学习路线\n\n正文示例", 888, categories, tags);
        createPost("Vue 3 + Vite 实战指南", "深度解析 Composition API 与 Vite", "技术分享", List.of("Vue3", "Vite"), LocalDate.parse("2026-01-20"), true,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-2.jpg", "# Vue3 + Vite\n\n正文示例", 650, categories, tags);
        createPost("Redis 进阶：缓存机制", "深入理解 Redis 生产问题", "后端", List.of("Redis", "缓存"), LocalDate.parse("2026-01-15"), true,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-3.jpg", "# Redis\n\n正文示例", 720, categories, tags);
        createPost("Spring Cloud 架构演进", "微服务核心知识点梳理", "面试", List.of("SpringCloud"), LocalDate.parse("2026-01-10"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-4.jpg", "# Spring Cloud\n\n正文示例", 530, categories, tags);
        createPost("算法精选：LeetCode 必刷", "如何高效掌握核心算法思想", "算法", List.of("算法", "刷题"), LocalDate.parse("2026-01-05"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-5.jpg", "# 算法\n\n正文示例", 430, categories, tags);
        createPost("前端性能优化 2026", "全面提升 Web 响应速度", "技术分享", List.of("性能", "前端"), LocalDate.parse("2026-01-01"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-6.jpg", "# 性能优化\n\n正文示例", 380, categories, tags);
        createPost("JUC 并发编程源码解析", "锁机制与 AQS 原理剖析", "后端", List.of("JUC", "多线程"), LocalDate.parse("2025-12-28"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-7.jpg", "# JUC\n\n正文示例", 500, categories, tags);
        createPost("Docker 容器化部署实践", "从镜像构建到集群化部署", "后端", List.of("Docker", "DevOps"), LocalDate.parse("2025-12-20"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-8.jpg", "# Docker\n\n正文示例", 460, categories, tags);
        createPost("微服务网关 Gateway 实战", "实现动态路由与鉴权控制", "后端", List.of("Gateway", "微服务"), LocalDate.parse("2025-12-15"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-9.jpg", "# Gateway\n\n正文示例", 420, categories, tags);
        createPost("消息队列 Kafka 原理详解", "高吞吐背后的架构设计", "后端", List.of("Kafka", "MQ"), LocalDate.parse("2025-12-10"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-10.jpg", "# Kafka\n\n正文示例", 410, categories, tags);
        createPost("MySQL 索引优化指南", "B+ 树原理与执行计划分析", "后端", List.of("MySQL", "数据库"), LocalDate.parse("2025-12-05"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-11.jpg", "# MySQL\n\n正文示例", 520, categories, tags);
        createPost("2025 年度成长总结", "在代码之外，寻找生活的意义", "生活", List.of("生活", "总结"), LocalDate.parse("2025-12-31"), false,
            "https://jway-blog.oss-cn-beijing.aliyuncs.com/blog/posts/post-12.jpg", "# 年度总结\n\n正文示例", 300, categories, tags);
    }

    private void createPost(String title,
                            String summary,
                            String category,
                            List<String> tagNames,
                            LocalDate date,
                            boolean isTop,
                            String coverUrl,
                            String content,
                            int views,
                            Map<String, Category> categories,
                            Map<String, Tag> tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setSummary(summary);
        post.setCategory(categories.get(category));
        post.setPublishDate(date);
        post.setTop(isTop);
        post.setCoverUrl(coverUrl);
        post.setContent(content);
        post.setViews(views);
        post.setTags(new HashSet<>(tagNames.stream().map(tags::get).toList()));
        postRepository.save(post);
    }

    private void seedWorks() {
        Work work1 = new Work();
        work1.setTitle("在线教育平台");
        work1.setImageUrl("https://images.unsplash.com/photo-1511512578047-dfb367046420?q=80&w=1200");
        work1.setDescription("融合课程学习、问答互动与推荐能力的在线平台。");
        work1.setPeriod("4个月");
        work1.setTeamSize("5人");
        work1.setTechs("SpringBoot|MySQL|Redis|Vue3");
        work1.setPoints("课程体系搭建|高并发热点优化");
        workRepository.save(work1);

        Work work2 = new Work();
        work2.setTitle("调度与工单系统");
        work2.setImageUrl("https://images.unsplash.com/photo-1522071820081-009f0129c71c?q=80&w=1200");
        work2.setDescription("企业级调度系统，支持工单流程与任务编排。");
        work2.setPeriod("6个月");
        work2.setTeamSize("30人");
        work2.setTechs("Java|Redis|xxl-job|Vue2");
        work2.setPoints("任务编排优化|告警链路完善");
        workRepository.save(work2);
    }

    private void seedSkills() {
        seedSkill("HTML5", "bi-filetype-html", "#e34f26", 1);
        seedSkill("CSS3", "bi-filetype-css", "#1572b6", 1);
        seedSkill("JavaScript", "bi-filetype-js", "#f7df1e", 1);
        seedSkill("Vue.js", "bi-vimeo", "#42b883", 1);
        seedSkill("Axios", "bi-arrow-left-right", "#5a29e4", 1);
        seedSkill("Element Plus", "bi-layers", "#409eff", 1);
        seedSkill("Sass", "bi-file-earmark-code", "#cc6699", 1);
        seedSkill("TypeScript", "bi-filetype-tsx", "#3178c6", 1);

        seedSkill("Java", "bi-cup-hot", "#ed8b00", 2);
        seedSkill("Spring Boot", "bi-leaf", "#6db33f", 2);
        seedSkill("MySQL", "bi-database-fill-gear", "#4479a1", 2);
        seedSkill("Redis", "bi-lightning-charge", "#dc382d", 2);
        seedSkill("RabbitMQ", "bi-send", "#ff6600", 2);
        seedSkill("MyBatis", "bi-server", "#000000", 2);
        seedSkill("Python", "bi-filetype-py", "#3776ab", 2);
        seedSkill("Docker", "bi-box-seam", "#2496ed", 2);
    }

    private void seedSkill(String name, String icon, String color, int type) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setIcon(icon);
        skill.setColor(color);
        skill.setType(type);
        skillRepository.save(skill);
    }

    private void seedMoments() {
        seedMoment("博客 V6 发布", "完善视觉风格并补齐统计与旧时光页面", LocalDate.parse("2026-03-01"), "primary", "large");
        seedMoment("主题与搜索优化", "新增深色模式和全站搜索体验", LocalDate.parse("2026-02-20"), "success", "default");
        seedMoment("项目启动", "完成 Vue3 + Vite 基础架构", LocalDate.parse("2026-02-01"), "primary", "large");
    }

    private void seedMoment(String title, String content, LocalDate date, String type, String size) {
        Moment m = new Moment();
        m.setTitle(title);
        m.setContent(content);
        m.setTimestamp(date);
        m.setType(type);
        m.setSize(size);
        momentRepository.save(m);
    }

    private void seedWebsiteLinks() {
        seedWebsiteLink("开发工具推荐", "bi bi-terminal", 1, "GitHub", "全球常用的代码托管与协作平台", "https://github.com/", "https://github.githubassets.com/favicons/favicon.svg", 1);
        seedWebsiteLink("开发工具推荐", "bi bi-terminal", 1, "Gitee", "代码托管与团队协作平台", "https://gitee.com/", "https://gitee.com/assets/favicon.ico", 2);
        seedWebsiteLink("开发工具推荐", "bi bi-terminal", 1, "Vite", "下一代前端构建工具", "https://cn.vitejs.dev/", "https://cn.vitejs.dev/logo.svg", 3);
        seedWebsiteLink("学习资源推荐", "bi bi-book", 2, "MDN Web Docs", "Web 开发权威文档", "https://developer.mozilla.org/", "https://developer.mozilla.org/favicon-48x48.cbbd161b.png", 1);
        seedWebsiteLink("学习资源推荐", "bi bi-book", 2, "LeetCode", "算法与面试练习平台", "https://leetcode.cn/", "https://leetcode.cn/favicon.ico", 2);
        seedWebsiteLink("实用工具推荐", "bi bi-tools", 3, "PDF24", "在线 PDF 工具集合", "https://tools.pdf24.org/zh/", "https://tools.pdf24.org/favicon.ico", 1);
    }

    private void seedWebsiteLink(String sectionTitle,
                                 String sectionIcon,
                                 int sectionOrder,
                                 String name,
                                 String description,
                                 String url,
                                 String logoUrl,
                                 int sortOrder) {
        WebsiteLink link = new WebsiteLink();
        link.setSectionTitle(sectionTitle);
        link.setSectionIcon(sectionIcon);
        link.setSectionOrder(sectionOrder);
        link.setName(name);
        link.setDescription(description);
        link.setUrl(url);
        link.setLogoUrl(logoUrl);
        link.setSortOrder(sortOrder);
        websiteLinkRepository.save(link);
    }
}
