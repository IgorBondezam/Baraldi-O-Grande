package com.example.todoapp.service;

import com.example.todoapp.domain.Task;
import com.example.todoapp.domain.enums.TaskPriority;
import com.example.todoapp.domain.User;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.repository.UserRepository;
import com.example.todoapp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }
    
    private void checkTaskOwnership(Task task) {
        User currentUser = getCurrentUser();
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar esta tarefa");
        }
    }

    public List<Task> findAllTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findByUser(currentUser);
    }

    public Task findTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com o ID: " + id));
        checkTaskOwnership(task);
        return task;
    }

    public List<Task> findTasksByCompleted(boolean completed) {
        User currentUser = getCurrentUser();
        return taskRepository.findByCompletedAndUser(completed, currentUser);
    }

    public List<Task> findTasksByPriority(TaskPriority priority) {
        User currentUser = getCurrentUser();
        return taskRepository.findByPriorityAndUser(priority, currentUser);
    }

    public List<Task> findOverdueTasks() {
        User currentUser = getCurrentUser();
        return taskRepository.findOverdueTasksByUser(currentUser);
    }

    public List<Task> findPendingTasksOrderByPriorityAndDueDate() {
        User currentUser = getCurrentUser();
        return taskRepository.findPendingTasksOrderByPriorityAndDueDateByUser(currentUser);
    }

    public List<Task> searchTasksByTitle(String title) {
        User currentUser = getCurrentUser();
        return taskRepository.findByTitleContainingIgnoreCaseAndUser(title, currentUser);
    }

    @Transactional
    public Task createTask(Task task) {
        User currentUser = getCurrentUser();
        task.setUser(currentUser);
        
        task.setCompleted(false);
        
        task.setCreatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Task task = findTaskById(id);

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }

    @Transactional
    public Task toggleTaskCompletion(Long id) {
        Task task = findTaskById(id);

        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = findTaskById(id);

        taskRepository.delete(task);
    }
}
