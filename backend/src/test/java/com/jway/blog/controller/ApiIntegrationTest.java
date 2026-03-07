package com.jway.blog.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "admin.api-prefix=/api/internal/cms",
    "admin.username=test-admin",
    "admin.password=test-pass",
    "admin.entry-key=test-entry-key"
})
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetPagedPosts() throws Exception {
        mockMvc.perform(get("/api/posts").param("page", "1").param("size", "9"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").value(12))
            .andExpect(jsonPath("$.data.list.length()").value(9));
    }

    @Test
    void shouldGetPostDetail() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.content").isNotEmpty());
    }

    @Test
    void shouldSearchPostsByCategory() throws Exception {
        mockMvc.perform(get("/api/posts/search").param("category", "后端"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(6));
    }

    @Test
    void shouldGetCategoriesAndTags() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").isNumber());

        mockMvc.perform(get("/api/tags"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").isNumber());
    }

    @Test
    void shouldGetWorksSkillsMomentsAndStatistics() throws Exception {
        mockMvc.perform(get("/api/works"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2));

        mockMvc.perform(get("/api/skills"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.frontend.length()").value(8))
            .andExpect(jsonPath("$.data.backend.length()").value(8));

        mockMvc.perform(get("/api/moments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(3));

        mockMvc.perform(get("/api/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalPosts").value(12));
    }

    @Test
    void shouldTrackVisitorsAndTotalViews() throws Exception {
        MvcResult first = mockMvc.perform(post("/api/track/visit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"path\":\"/\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalVisitors").value(1))
            .andExpect(jsonPath("$.data.totalViews").value(1))
            .andReturn();

        JsonNode firstData = objectMapper.readTree(first.getResponse().getContentAsString()).get("data");
        String visitorId = firstData.get("visitorId").asText();

        mockMvc.perform(post("/api/track/visit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visitorId\":\"" + visitorId + "\",\"path\":\"/post/1\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalVisitors").value(1))
            .andExpect(jsonPath("$.data.totalViews").value(2));

        mockMvc.perform(get("/api/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.totalVisitors").value(1))
            .andExpect(jsonPath("$.data.totalViews").value(2));
    }

    @Test
    void shouldSupportAdminPostCrud() throws Exception {
        String token = loginAndGetToken();

        MvcResult created = mockMvc.perform(post("/api/internal/cms/posts")
                .header("Authorization", "Bearer " + token)
                .header("X-Admin-Entry-Key", "test-entry-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title":"后台发布测试文章",
                      "summary":"用于验证后台发布流程",
                      "content":"# Admin 发布\\n\\n内容",
                      "cover":"https://images.unsplash.com/photo-1516116216624-53e697fedbea?q=80&w=1200",
                      "date":"2026-03-06",
                      "category":"后台发布",
                      "tags":["后台","发布"],
                      "isTop":true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("后台发布测试文章"))
            .andReturn();

        long postId = objectMapper.readTree(created.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(put("/api/internal/cms/posts/{id}", postId)
                .header("Authorization", "Bearer " + token)
                .header("X-Admin-Entry-Key", "test-entry-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title":"后台发布测试文章-更新",
                      "summary":"更新后的摘要",
                      "content":"# Updated",
                      "cover":"https://images.unsplash.com/photo-1516116216624-53e697fedbea?q=80&w=1200",
                      "date":"2026-03-06",
                      "category":"后台发布",
                      "tags":["后台","更新"],
                      "isTop":false
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("后台发布测试文章-更新"));

        mockMvc.perform(get("/api/internal/cms/posts")
                .header("Authorization", "Bearer " + token)
                .header("X-Admin-Entry-Key", "test-entry-key"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(13));
    }

    private String loginAndGetToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/internal/cms/login")
                .header("X-Admin-Entry-Key", "test-entry-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username":"test-admin",
                      "password":"test-pass"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("token").asText();
    }
}
