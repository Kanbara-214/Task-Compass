package com.kanbara.taskcompass.model;

import java.util.List;

public record TaskPageView(
		List<TaskView> tasks,
		int currentPage,
		int size,
		int totalCount,
		int totalPages) {

	public boolean hasPrevious() {
		return currentPage > 1;
	}

	public boolean hasNext() {
		return currentPage < totalPages;
	}

	public int previousPage() {
		return Math.max(1, currentPage - 1);
	}

	public int nextPage() {
		return Math.min(totalPages, currentPage + 1);
	}
}
