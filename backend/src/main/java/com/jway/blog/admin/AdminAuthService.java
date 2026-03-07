package com.jway.blog.admin;

import com.jway.blog.admin.dto.AdminLoginDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminAuthService {

    private final String adminUsername;
    private final String adminPassword;
    private final String adminPasswordHash;
    private final String adminEntryKey;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, LocalDateTime> tokenStore = new ConcurrentHashMap<>();

    public AdminAuthService(@Value("${admin.username}") String adminUsername,
                            @Value("${admin.password:}") String adminPassword,
                            @Value("${admin.password-hash:}") String adminPasswordHash,
                            @Value("${admin.entry-key}") String adminEntryKey) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminPasswordHash = adminPasswordHash;
        this.adminEntryKey = adminEntryKey;
        if ((adminPassword == null || adminPassword.isBlank())
            && (adminPasswordHash == null || adminPasswordHash.isBlank())) {
            throw new IllegalStateException("admin.password or admin.password-hash must be configured");
        }
    }

    public AdminLoginDto login(String username, String password, String entryKey) {
        ensureEntryKey(entryKey);
        if (!safeEquals(adminUsername, username) || !matchesPassword(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid admin credentials");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, LocalDateTime.now().plusHours(12));
        return new AdminLoginDto(token, adminUsername);
    }

    public void ensureEntryKey(String entryKey) {
        if (entryKey == null || entryKey.isBlank() || !safeEquals(adminEntryKey, entryKey.trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid admin entry key");
        }
    }

    public boolean validate(String token) {
        LocalDateTime expireAt = tokenStore.get(token);
        if (expireAt == null) {
            return false;
        }
        if (expireAt.isBefore(LocalDateTime.now())) {
            tokenStore.remove(token);
            return false;
        }
        return true;
    }

    private boolean matchesPassword(String rawPassword) {
        if (rawPassword == null) {
            return false;
        }
        if (adminPasswordHash != null && !adminPasswordHash.isBlank()) {
            try {
                return passwordEncoder.matches(rawPassword, adminPasswordHash);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return safeEquals(adminPassword, rawPassword);
    }

    private boolean safeEquals(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }
}
