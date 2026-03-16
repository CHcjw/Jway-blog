package com.jway.blog.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.jway.blog.config.OssProperties;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OssMediaService {

    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s)\"'>]+");
    private static final Pattern OSS_HOST_PATTERN = Pattern.compile(".*\\.oss-[^.]+\\.aliyuncs\\.com");

    private final OssProperties ossProperties;
    private volatile OSS ossClient;

    public OssMediaService(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    public boolean enabled() {
        return sdkEnabled();
    }

    private boolean sdkEnabled() {
        return notBlank(ossProperties.endpoint())
            && notBlank(ossProperties.bucket())
            && notBlank(ossProperties.accessKeyId())
            && notBlank(ossProperties.accessKeySecret());
    }

    public String toProxyUrl(String rawUrl) {
        String key = extractOssKey(rawUrl);
        if (key == null) return rawUrl;
        return "/api/media/object?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);
    }

    public String rewriteTextUrls(String text) {
        if (text == null || text.isBlank()) return text;
        Matcher matcher = URL_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replaced = toProxyUrl(matcher.group());
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replaced));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public OssObjectStream getObjectStream(String key) {
        String normalized = normalizeKey(key);
        if (sdkEnabled()) {
            OSSObject object = getClient().getObject(ossProperties.bucket(), normalized);
            ObjectMetadata metadata = object.getObjectMetadata();
            return new OssObjectStream(object.getObjectContent(), metadata.getContentType(), metadata.getContentLength());
        }
        return getPublicObjectStream(normalized);
    }

    private String extractOssKey(String rawUrl) {
        if (!notBlank(rawUrl)) return null;
        try {
            URI uri = URI.create(rawUrl.trim());
            String host = lower(uri.getHost());
            if (!matchesConfiguredHost(host)) {
                return null;
            }
            String path = uri.getPath();
            if (!notBlank(path)) return null;
            return normalizeKey(path);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean matchesConfiguredHost(String host) {
        if (!notBlank(host)) return false;
        String explicitHost = lower(stripScheme(ossProperties.publicHost()));
        if (notBlank(explicitHost) && host.equals(explicitHost)) {
            return true;
        }
        String endpointHost = lower(stripScheme(ossProperties.endpoint()));
        if (notBlank(endpointHost) && notBlank(ossProperties.bucket())) {
            String defaultHost = lower(ossProperties.bucket() + "." + endpointHost);
            if (host.equals(defaultHost)) {
                return true;
            }
        }
        return OSS_HOST_PATTERN.matcher(host).matches();
    }

    private OssObjectStream getPublicObjectStream(String key) {
        try {
            String host = resolvePublicHost();
            if (!notBlank(host)) {
                throw new IllegalStateException("oss public host is not configured");
            }
            URL url = new URL("https://" + host + "/" + key);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(10_000);
            String contentType = connection.getContentType();
            long length = connection.getContentLengthLong();
            return new OssObjectStream(connection.getInputStream(), contentType, length);
        } catch (Exception ex) {
            throw new IllegalStateException("failed to load media from public oss", ex);
        }
    }

    private String resolvePublicHost() {
        String explicitHost = stripScheme(ossProperties.publicHost());
        if (notBlank(explicitHost)) {
            return explicitHost;
        }
        String endpointHost = stripScheme(ossProperties.endpoint());
        if (notBlank(endpointHost) && notBlank(ossProperties.bucket())) {
            return ossProperties.bucket() + "." + endpointHost;
        }
        return "";
    }

    private OSS getClient() {
        OSS local = ossClient;
        if (local != null) return local;
        synchronized (this) {
            if (ossClient == null) {
                ossClient = new OSSClientBuilder().build(
                    stripScheme(ossProperties.endpoint()),
                    ossProperties.accessKeyId(),
                    ossProperties.accessKeySecret()
                );
            }
            return ossClient;
        }
    }

    private String normalizeKey(String key) {
        String normalized = key.trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.contains("..")) {
            throw new IllegalArgumentException("invalid oss key");
        }
        return normalized;
    }

    private String stripScheme(String value) {
        if (!notBlank(value)) return "";
        return value
            .replaceFirst("^https?://", "")
            .replaceAll("/+$", "")
            .trim();
    }

    private String lower(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    @PreDestroy
    public void shutdown() {
        OSS local = ossClient;
        if (local != null) {
            local.shutdown();
        }
    }

    public record OssObjectStream(InputStream body, String contentType, long contentLength) {
    }
}
