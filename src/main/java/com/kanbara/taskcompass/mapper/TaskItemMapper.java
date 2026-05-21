package com.kanbara.taskcompass.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.query.TaskListQuery;

@Mapper
public interface TaskItemMapper {

	int countByOwnerIdAndListQuery(
			@Param("ownerId") Long ownerId,
			@Param("query") TaskListQuery query);

	int countByOwnerId(Long ownerId);

	int countByOwnerIdAndStatus(
			@Param("ownerId") Long ownerId,
			@Param("status") TaskStatus status);

	int countActiveByOwnerId(Long ownerId);

	List<TaskItem> findActiveByOwnerId(
			@Param("ownerId") Long ownerId);

	List<TaskItem> findByOwnerIdAndListQuery(
			@Param("ownerId") Long ownerId,
			@Param("query") TaskListQuery query);

	List<TaskItem> findOverdueTopByOwnerId(
			@Param("ownerId") Long ownerId,
			@Param("limit") int limit);

	TaskItem findByIdAndOwnerId(@Param("taskId") Long taskId, @Param("ownerId") Long ownerId);

	List<String> findDistinctCategoriesByOwnerId(Long ownerId);

	int insert(TaskItem task);

	int update(TaskItem task);

	int deleteByIdAndOwnerId(@Param("taskId") Long taskId, @Param("ownerId") Long ownerId);

	int deleteByOwnerId(Long ownerId);
}
