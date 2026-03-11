package com.jway.blog.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownTocUtilTest {

    @Test
    void shouldGenerateTocFromHeadings() {
        String content = """
            # Intro

            ## Setup
            text
            ### Details
            """;

        String result = MarkdownTocUtil.upsertToc(content);

        assertTrue(result.startsWith("## \u76EE\u5F55"));
        assertTrue(result.contains("- [Intro](#intro)"));
        assertTrue(result.contains("  - [Setup](#setup)"));
        assertTrue(result.contains("    - [Details](#details)"));
    }

    @Test
    void shouldAppendIndexForDuplicateHeadingAnchors() {
        String content = """
            ## Repeat
            body
            ## Repeat
            """;

        String result = MarkdownTocUtil.upsertToc(content);

        assertTrue(result.contains("- [Repeat](#repeat)"));
        assertTrue(result.contains("- [Repeat](#repeat-2)"));
    }

    @Test
    void shouldReplaceExistingTocBeforeGenerating() {
        String content = """
            ## \u76EE\u5F55
            - [Old](#old)

            # New Title
            """;

        String result = MarkdownTocUtil.upsertToc(content);

        assertTrue(result.contains("- [New Title](#new-title)"));
        assertTrue(!result.contains("[Old](#old)"));
        assertEquals(1, countOccurrences(result, "## \u76EE\u5F55"));
    }

    private int countOccurrences(String source, String target) {
        int count = 0;
        int from = 0;
        while (true) {
            int idx = source.indexOf(target, from);
            if (idx < 0) {
                return count;
            }
            count += 1;
            from = idx + target.length();
        }
    }
}
