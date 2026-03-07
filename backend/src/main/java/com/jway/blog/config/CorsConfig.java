package com.jway.blog.config;

import com.jway.blog.admin.AdminAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final String adminApiPrefix;

    public CorsConfig(AdminAuthInterceptor adminAuthInterceptor,
                      @Value("${admin.api-prefix}") String adminApiPrefix) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.adminApiPrefix = adminApiPrefix;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(false)
            .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor).addPathPatterns(adminApiPrefix + "/**");
    }
}
