package com.kanbara.taskcompass.mapper;

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
import com.kanbara.taskcompass.model.TaskSortOption;

@Mapper
public interface TaskItemMapper {

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
            order by updated_at desc
            """)
    List<TaskItem> findByOwnerIdOrderByUpdatedAtDesc(Long ownerId);

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
                updated_at
            from tasks
            where owner_id = #{ownerId}
            <if test="status != null">
              and status = #{status}
            </if>
            <if test="category != null and category != ''">
              and lower(category) = lower(#{category})
            </if>
            <choose>
              <when test="sort.slug == 'deadline'">
                order by due_date asc, importance desc, urgency desc, updated_at desc
              </when>
              <when test="sort.slug == 'priority'">
                order by importance desc, urgency desc, due_date asc, updated_at desc
              </when>
              <when test="sort.slug == 'updated'">
                order by updated_at desc
              </when>
              <otherwise>
                order by updated_at desc
              </otherwise>
            </choose>
            </script>
            """)
    List<TaskItem> findByOwnerIdAndListQuery(
            @Param("ownerId") Long ownerId,
            @Param("status") TaskStatus status,
            @Param("category") String category,
            @Param("sort") TaskSortOption sort);

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
