package com.example.todoapp.controller;

import com.example.todoapp.domain.Task;
import com.example.todoapp.domain.enums.TaskPriority;
import com.example.todoapp.dto.TaskDTO;
import com.example.todoapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Listar todas as tarefas", description = "Lista todas as tarefas do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente")
    })
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<Task> tasks = taskService.findAllTasks();
        return ResponseEntity.ok(TaskDTO.fromEntityList(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        Task task = taskService.findTaskById(id);
        return ResponseEntity.ok(TaskDTO.fromEntity(task));
    }

    @GetMapping("/status")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@RequestParam boolean completed) {
        List<Task> tasks = taskService.findTasksByCompleted(completed);
        return ResponseEntity.ok(TaskDTO.fromEntityList(tasks));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskDTO>> getTasksByPriority(@PathVariable TaskPriority priority) {
        List<Task> tasks = taskService.findTasksByPriority(priority);
        return ResponseEntity.ok(TaskDTO.fromEntityList(tasks));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
        List<Task> tasks = taskService.findOverdueTasks();
        return ResponseEntity.ok(TaskDTO.fromEntityList(tasks));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TaskDTO>> getPendingTasksOrderByPriorityAndDueDate() {
        List<Task> tasks = taskService.findPendingTasksOrderByPriorityAndDueDate();
        return ResponseEntity.ok(TaskDTO.fromEntityList(tasks));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> searchTasksByTitle(@RequestParam String title) {
        List<Task> tasks = taskService.searchTasksByTitle(title);
        return ResponseEntity.ok(TaskDTO.fromEntityList(tasks));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task task = taskDTO.toEntity();
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskDTO.fromEntity(createdTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        Task existingTask = taskService.findTaskById(id);
        Task updatedTask = taskService.updateTask(id, taskDTO.updateEntity(existingTask));
        return ResponseEntity.ok(TaskDTO.fromEntity(updatedTask));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskDTO> toggleTaskCompletion(@PathVariable Long id) {
        Task task = taskService.toggleTaskCompletion(id);
        return ResponseEntity.ok(TaskDTO.fromEntity(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
