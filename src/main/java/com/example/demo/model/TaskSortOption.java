package com.example.demo.model;

import java.util.Arrays;
import java.util.Locale;

public enum TaskSortOption {
    RECOMMENDED("recommended", "おすすめ順"),
    DEADLINE("deadline", "締切順"),
    PRIORITY("priority", "重要度順"),
    UPDATED("updated", "更新順");

    private final String slug;
    private final String label;

    TaskSortOption(String slug, String label) {
        this.slug = slug;
        this.label = label;
    }

    public String getSlug() {
        return slug;
    }

    public String getLabel() {
        return label;
    }

    public static TaskSortOption from(String raw) {
        if (raw == null || raw.isBlank()) {
            return RECOMMENDED;
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(option -> option.slug.equals(normalized))
                .findFirst()
                .orElse(RECOMMENDED);
    }
}
