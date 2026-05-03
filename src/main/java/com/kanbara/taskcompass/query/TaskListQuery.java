package com.kanbara.taskcompass.query;

import java.util.Locale;

import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.model.TaskSortOption;

public record TaskListQuery(
		String category,
		TaskStatus status,
		TaskSortOption sort,
		Integer page,
		Integer size) {

	private static final int DEFAULT_PAGE = 1;
	private static final int MIN_PAGE_SIZE = 1;
	private static final int MAX_PAGE_SIZE = 50;

	public static TaskListQuery of(String category, String status, String sort, int page, int size) {
		String normalizedCategory = category == null ? "" : category.trim();
		TaskStatus normalizedStatus = parseStatus(status);
		TaskSortOption normalizedSort = TaskSortOption.from(sort);
		int normalizedPage = Math.max(DEFAULT_PAGE, page);
		int normalizedSize = Math.min(MAX_PAGE_SIZE, Math.max(MIN_PAGE_SIZE, size));
		return new TaskListQuery(normalizedCategory, normalizedStatus, normalizedSort, normalizedPage, normalizedSize);
	}

	public static TaskListQuery unpaged(TaskSortOption sort) {
		TaskSortOption normalizedSort = sort == null ? TaskSortOption.RECOMMENDED : sort;
		return new TaskListQuery("", null, normalizedSort, null, null);
	}

	public boolean hasCategory() {
		return !category.isBlank();
	}

	private static TaskStatus parseStatus(String status) {
		if (status == null || status.isBlank()) {
			return null;
		}

		try {
			return TaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	public int offset() {
		if (page == null || size == null) {
			throw new IllegalArgumentException("Page and size must not be null.");
		}
		return (page - 1) * size;
	}
}
