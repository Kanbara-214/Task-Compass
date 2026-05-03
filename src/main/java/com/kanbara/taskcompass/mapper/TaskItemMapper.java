package com.kanbara.taskcompass.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.query.TaskListQuery;

@Mapper
public interface TaskItemMapper {

	@Select("""
			<script>
			select count(*)
			from tasks
			where owner_id = #{ownerId}
			<if test="query.status != null">
			  and status = #{query.status}
			</if>
			<if test="query.category != null and query.category != ''">
			  and lower(category) = lower(#{query.category})
			</if>
			</script>
			""")
	int countByOwnerIdAndListQuery(
			@Param("ownerId") Long ownerId,
			@Param("query") TaskListQuery query);

	@Select("""
			select count(*)
			from tasks
			where owner_id = #{ownerId}
			""")
	int countByOwnerId(Long ownerId);

	@Select("""
			select count(*)
			from tasks
			where owner_id = #{ownerId}
			  and status = #{status}
			""")
	int countByOwnerIdAndStatus(
			@Param("ownerId") Long ownerId,
			@Param("status") TaskStatus status);

	@Select("""
			select count(*)
			from tasks
			where owner_id = #{ownerId}
			  and status <> 'DONE'
			""")
	int countActiveByOwnerId(Long ownerId);

	@Select("""
			select coalesce(round(avg(
			    case
			      when status = 'DONE' then 0
			      else
			          importance * 12
			        + urgency * 9
			        + case
			            when due_date <= current_date - 4 then 75
			            when due_date <= current_date - 3 then 70
			            when due_date <= current_date - 2 then 65
			            when due_date < current_date then 60
			            when due_date = current_date then 36
			            when due_date = current_date + 1 then 28
			            when due_date <= current_date + 3 then 20
			            when due_date <= current_date + 7 then 12
			            when due_date <= current_date + 14 then 6
			            else 0
			          end
			        + case
			            when estimated_minutes >= 180 then 8
			            when estimated_minutes >= 90 then 4
			            else 0
			          end
			        + case
			            when status = 'IN_PROGRESS' then 6
			            else 0
			          end
			    end
			)), 0)
			from tasks
			where owner_id = #{ownerId}
			  and status <> 'DONE'
			""")
	int averageActivePriorityScoreByOwnerId(Long ownerId);

	@Select("""
			<script>
			select
			    id,
			    owner_id,
			    title,
			    description,
			    due_date,
			    importance,
			    urgency,
			    estimated_minutes,
			    status,
			    category,
			    created_at,
			    updated_at,
			    case
			      when status = 'DONE' then 0
			      else
			          importance * 12
			        + urgency * 9
			        + case
			            when due_date &lt;= current_date - 4 then 75
			            when due_date &lt;= current_date - 3 then 70
			            when due_date &lt;= current_date - 2 then 65
			            when due_date &lt; current_date then 60
			            when due_date = current_date then 36
			            when due_date = current_date + 1 then 28
			            when due_date &lt;= current_date + 3 then 20
			            when due_date &lt;= current_date + 7 then 12
			            when due_date &lt;= current_date + 14 then 6
			            else 0
			          end
			        + case
			            when estimated_minutes &gt;= 180 then 8
			            when estimated_minutes &gt;= 90 then 4
			            else 0
			          end
			        + case
			            when status = 'IN_PROGRESS' then 6
			            else 0
			          end
			    end as recommended_score
			from tasks
			where owner_id = #{ownerId}
			<if test="query.status != null">
			  and status = #{query.status}
			</if>
			<if test="query.category != null and query.category != ''">
			  and lower(category) = lower(#{query.category})
			</if>
			<choose>
			  <when test="query.sort.slug == 'deadline'">
			    order by due_date asc, importance desc, urgency desc, updated_at desc
			  </when>
			  <when test="query.sort.slug == 'priority'">
			    order by importance desc, urgency desc, due_date asc, updated_at desc
			  </when>
			  <when test="query.sort.slug == 'updated'">
			    order by updated_at desc
			  </when>
			  <otherwise>
			    order by recommended_score desc,
			             due_date asc,
			             importance desc,
			             urgency desc,
			             updated_at desc
			  </otherwise>
			</choose>
			<if test="query.page != null and query.size != null">
			  limit #{query.size}
			  offset #{query.offset}
			</if>
			</script>
			""")
	List<TaskItem> findByOwnerIdAndListQuery(
			@Param("ownerId") Long ownerId,
			@Param("query") TaskListQuery query);

	@Select("""
			select
			    id,
			    owner_id,
			    title,
			    description,
			    due_date,
			    importance,
			    urgency,
			    estimated_minutes,
			    status,
			    category,
			    created_at,
			    updated_at,
			    case
			      when status = 'DONE' then 0
			      else
			          importance * 12
			        + urgency * 9
			        + case
			            when due_date <= current_date - 4 then 75
			            when due_date <= current_date - 3 then 70
			            when due_date <= current_date - 2 then 65
			            when due_date < current_date then 60
			            when due_date = current_date then 36
			            when due_date = current_date + 1 then 28
			            when due_date <= current_date + 3 then 20
			            when due_date <= current_date + 7 then 12
			            when due_date <= current_date + 14 then 6
			            else 0
			          end
			        + case
			            when estimated_minutes >= 180 then 8
			            when estimated_minutes >= 90 then 4
			            else 0
			          end
			        + case
			            when status = 'IN_PROGRESS' then 6
			            else 0
			          end
			    end as recommended_score
			from tasks
			where owner_id = #{ownerId}
			  and status <> 'DONE'
			order by recommended_score desc,
			         due_date asc,
			         importance desc,
			         urgency desc,
			         updated_at desc
			limit #{limit}
			""")
	List<TaskItem> findRecommendedTopByOwnerId(
			@Param("ownerId") Long ownerId,
			@Param("limit") int limit);

	@Select("""
			select
			    id,
			    owner_id,
			    title,
			    description,
			    due_date,
			    importance,
			    urgency,
			    estimated_minutes,
			    status,
			    category,
			    created_at,
			    updated_at
			from tasks
			where owner_id = #{ownerId}
			  and status <> 'DONE'
			  and due_date < current_date
			order by due_date asc,
			         importance desc,
			         urgency desc,
			         updated_at desc
			limit #{limit}
			""")
	List<TaskItem> findOverdueTopByOwnerId(
			@Param("ownerId") Long ownerId,
			@Param("limit") int limit);

	@Select("""
			select
			    id,
			    owner_id,
			    title,
			    description,
			    due_date,
			    importance,
			    urgency,
			    estimated_minutes,
			    status,
			    category,
			    created_at,
			    updated_at
			from tasks
			where owner_id = #{ownerId}
			  and status <> 'DONE'
			  and due_date >= #{startDate}
			  and due_date <= #{endDate}
			order by due_date asc,
			         importance desc,
			         urgency desc,
			         updated_at desc
			limit #{limit}
			""")
	List<TaskItem> findDueBetweenTopByOwnerId(
			@Param("ownerId") Long ownerId,
			@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate,
			@Param("limit") int limit);

	@Select("""
			select
			    id,
			    owner_id,
			    title,
			    description,
			    due_date,
			    importance,
			    urgency,
			    estimated_minutes,
			    status,
			    category,
			    created_at,
			    updated_at
			from tasks
			where id = #{taskId}
			  and owner_id = #{ownerId}
			""")
	TaskItem findByIdAndOwnerId(@Param("taskId") Long taskId, @Param("ownerId") Long ownerId);

	@Select("""
			select distinct category
			from tasks
			where owner_id = #{ownerId}
			  and category is not null
			  and category <> ''
			order by category
			""")
	List<String> findDistinctCategoriesByOwnerId(Long ownerId);

	@Insert("""
			insert into tasks (
			    owner_id,
			    title,
			    description,
			    due_date,
			    importance,
			    urgency,
			    estimated_minutes,
			    status,
			    category,
			    created_at,
			    updated_at
			) values (
			    #{ownerId},
			    #{title},
			    #{description},
			    #{dueDate},
			    #{importance},
			    #{urgency},
			    #{estimatedMinutes},
			    #{status},
			    #{category},
			    #{createdAt},
			    #{updatedAt}
			)
			""")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int insert(TaskItem task);

	@Update("""
			update tasks
			set title = #{title},
			    description = #{description},
			    due_date = #{dueDate},
			    importance = #{importance},
			    urgency = #{urgency},
			    estimated_minutes = #{estimatedMinutes},
			    status = #{status},
			    category = #{category},
			    updated_at = #{updatedAt}
			where id = #{id}
			  and owner_id = #{ownerId}
			""")
	int update(TaskItem task);

	@Delete("""
			delete from tasks
			where id = #{taskId}
			  and owner_id = #{ownerId}
			""")
	int deleteByIdAndOwnerId(@Param("taskId") Long taskId, @Param("ownerId") Long ownerId);
}
