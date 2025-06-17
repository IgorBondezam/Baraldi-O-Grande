package com.example.todoapp.graphql;

import com.example.todoapp.domain.Task;
import com.example.todoapp.domain.enums.TaskPriority;
import com.example.todoapp.domain.User;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.services.UserDetailsImpl;
import com.example.todoapp.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TaskGraphQLController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> getAllTasks() {
        return taskService.findAllTasks();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Task getTaskById(@Argument Long id) {
        return taskService.findTaskById(id);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> getTasksByStatus(@Argument boolean completed) {
        return taskService.findTasksByCompleted(completed);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> getTasksByPriority(@Argument TaskPriority priority) {
        return taskService.findTasksByPriority(priority);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> getOverdueTasks() {
        return taskService.findOverdueTasks();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> getPendingTasks() {
        return taskService.findPendingTasksOrderByPriorityAndDueDate();
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<Task> searchTasks(@Argument String title) {
        return taskService.searchTasksByTitle(title);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Task createTask(@Argument TaskInput input) {
        Task task = new Task();
        task.setTitle(input.getTitle());
        task.setDescription(input.getDescription());
        task.setCompleted(input.isCompleted());
        task.setDueDate(input.getDueDate());
        task.setPriority(input.getPriority());
        
        return taskService.createTask(task);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Task updateTask(@Argument Long id, @Argument TaskUpdateInput input) {
        Task task = taskService.findTaskById(id);
        
        if (input.getTitle() != null) {
            task.setTitle(input.getTitle());
        }
        if (input.getDescription() != null) {
            task.setDescription(input.getDescription());
        }
        if (input.getCompleted() != null) {
            task.setCompleted(input.getCompleted());
        }
        if (input.getDueDate() != null) {
            task.setDueDate(input.getDueDate());
        }
        if (input.getPriority() != null) {
            task.setPriority(input.getPriority());
        }
        
        return taskService.updateTask(id, task);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Task toggleTaskCompletion(@Argument Long id) {
        return taskService.toggleTaskCompletion(id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteTask(@Argument Long id) {
        taskService.deleteTask(id);
        return true;
    }
}
