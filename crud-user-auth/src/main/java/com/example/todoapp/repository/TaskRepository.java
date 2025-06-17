package com.example.todoapp.repository;

import com.example.todoapp.domain.Task;
import com.example.todoapp.domain.enums.TaskPriority;
import com.example.todoapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUser(User user);
    
    List<Task> findByCompletedAndUser(boolean completed, User user);
    
    List<Task> findByPriorityAndUser(TaskPriority priority, User user);
    
    List<Task> findByDueDateBeforeAndUser(LocalDateTime date, User user);
    
    List<Task> findByTitleContainingIgnoreCaseAndUser(String title, User user);
    
    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.dueDate < CURRENT_TIMESTAMP AND t.user = :user")
    List<Task> findOverdueTasksByUser(@Param("user") User user);
    
    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.user = :user ORDER BY " +
           "CASE WHEN t.priority = 'HIGH' THEN 0 " +
           "WHEN t.priority = 'MEDIUM' THEN 1 " +
           "ELSE 2 END, " +
           "t.dueDate ASC NULLS LAST")
    List<Task> findPendingTasksOrderByPriorityAndDueDateByUser(@Param("user") User user);
    
    List<Task> findByCompleted(boolean completed);
    
    List<Task> findByPriority(TaskPriority priority);
    
    List<Task> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.dueDate < CURRENT_TIMESTAMP")
    List<Task> findOverdueTasks();
    
    @Query("SELECT t FROM Task t WHERE t.completed = false ORDER BY " +
           "CASE WHEN t.priority = 'HIGH' THEN 0 " +
           "WHEN t.priority = 'MEDIUM' THEN 1 " +
           "ELSE 2 END, " +
           "t.dueDate ASC NULLS LAST")
    List<Task> findPendingTasksOrderByPriorityAndDueDate();
}
