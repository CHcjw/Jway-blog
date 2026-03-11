package com.jway.blog.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkdownTocUtil {

    private static final String TOC_TITLE = "\u76EE\u5F55";
    private static final Pattern TOC_HEADING_PATTERN = Pattern.compile("^##\\s*" + TOC_TITLE + "\\s*$");
    private static final Pattern TOC_ITEM_PATTERN = Pattern.compile("^\\s*[-*]\\s+\\[[^\\]]+\\]\\(#.+\\)\\s*$");
    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,3})\\s+(.+?)\\s*$");
    private static final Pattern SLUG_INVALID_PATTERN = Pattern.compile("[^\\w\\u4e00-\\u9fa5]+");
    private static final Pattern LEADING_DASH_PATTERN = Pattern.compile("^-+");
    private static final Pattern TRAILING_DASH_PATTERN = Pattern.compile("-+$");

    private MarkdownTocUtil() {
    }

    public static String upsertToc(String content) {
        String baseContent = stripExistingToc(content);
        List<Heading> headings = extractHeadings(baseContent);
        if (headings.isEmpty()) {
            return baseContent;
        }

        List<String> tocLines = new ArrayList<>();
        tocLines.add("## " + TOC_TITLE);
        for (Heading heading : headings) {
            tocLines.add("  ".repeat(Math.max(heading.level() - 1, 0)) + "- [" + heading.title() + "](#" + heading.id() + ")");
        }

        String normalizedContent = baseContent.replaceFirst("^\\s+", "");
        return String.join("\n", tocLines) + "\n\n" + normalizedContent;
    }

    private static String stripExistingToc(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        String[] lines = content.split("\\r?\\n", -1);
        int start = -1;
        for (int i = 0; i < lines.length; i++) {
            if (TOC_HEADING_PATTERN.matcher(lines[i].trim()).matches()) {
                start = i;
                break;
            }
        }
        if (start < 0) {
            return content;
        }

        int end = start + 1;
        while (end < lines.length) {
            String line = lines[end];
            String trimmed = line.trim();
            if (trimmed.isEmpty() || TOC_ITEM_PATTERN.matcher(line).matches()) {
                end += 1;
                continue;
            }
            break;
        }
        while (end < lines.length && lines[end].trim().isEmpty()) {
            end += 1;
        }

        List<String> remain = new ArrayList<>();
        for (int i = 0; i < start; i++) {
            remain.add(lines[i]);
        }
        for (int i = end; i < lines.length; i++) {
            remain.add(lines[i]);
        }
        return String.join("\n", remain);
    }

    private static List<Heading> extractHeadings(String content) {
        List<Heading> headings = new ArrayList<>();
        Map<String, Integer> used = new HashMap<>();
        boolean inCodeFence = false;

        String source = content == null ? "" : content;
        for (String line : source.split("\\r?\\n", -1)) {
            String trimmed = line.trim();
            if (trimmed.startsWith("```")) {
                inCodeFence = !inCodeFence;
                continue;
            }
            if (inCodeFence) {
                continue;
            }

            Matcher matcher = HEADING_PATTERN.matcher(trimmed);
            if (!matcher.matches()) {
                continue;
            }

            int level = matcher.group(1).length();
            String title = matcher.group(2).trim();
            if (title.isEmpty()) {
                continue;
            }

            String base = slugify(title);
            int count = used.getOrDefault(base, 0) + 1;
            used.put(base, count);
            String id = count == 1 ? base : base + "-" + count;
            headings.add(new Heading(level, title, id));
        }

        return headings;
    }

    private static String slugify(String title) {
        String anchor = title == null ? "" : title.trim().toLowerCase();
        anchor = SLUG_INVALID_PATTERN.matcher(anchor).replaceAll("-");
        anchor = LEADING_DASH_PATTERN.matcher(anchor).replaceAll("");
        anchor = TRAILING_DASH_PATTERN.matcher(anchor).replaceAll("");
        return anchor.isEmpty() ? "section" : anchor;
    }

    private record Heading(int level, String title, String id) {
    }
}
