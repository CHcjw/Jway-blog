package com.jway.blog.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminAuthService adminAuthService;
    private final String adminPrefix;

    public AdminAuthInterceptor(AdminAuthService adminAuthService,
                                @Value("${admin.api-prefix}") String adminPrefix) {
        this.adminAuthService = adminAuthService;
        this.adminPrefix = adminPrefix;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if ((adminPrefix + "/login").equals(path)) {
            return true;
        }

        try {
            adminAuthService.ensureEntryKey(request.getHeader("X-Admin-Entry-Key"));
        } catch (Exception ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String token = auth.substring(7).trim();
        if (!adminAuthService.validate(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }
}
