package com.example.todoapp.dto;

import com.example.todoapp.domain.Task;
import com.example.todoapp.domain.enums.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {
    private Long id;
    
    @NotBlank(message = "O título da tarefa é obrigatório")
    private String title;
    
    private String description;
    
    private boolean completed;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime dueDate;
    
    private TaskPriority priority;
    
    /**
     * Converte uma entidade Task para TaskDTO
     * @param task entidade a ser convertida
     * @return TaskDTO correspondente
     */
    public static TaskDTO fromEntity(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .build();
    }
    
    /**
     * Converte uma lista de entidades Task para lista de TaskDTO
     * @param tasks lista de entidades a serem convertidas
     * @return lista de TaskDTOs correspondentes
     */
    public static List<TaskDTO> fromEntityList(List<Task> tasks) {
        return tasks.stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Converte um TaskDTO para entidade Task
     * @return entidade Task correspondente
     */
    public Task toEntity() {
        Task task = new Task();
        task.setId(this.id);
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setCompleted(this.completed);
        task.setDueDate(this.dueDate);
        task.setPriority(this.priority != null ? this.priority : TaskPriority.MEDIUM);
        return task;
    }
    
    /**
     * Atualiza uma entidade Task existente com os dados do DTO
     * @param existingTask entidade a ser atualizada
     * @return entidade Task atualizada
     */
    public Task updateEntity(Task existingTask) {
        existingTask.setTitle(this.title);
        existingTask.setDescription(this.description);
        existingTask.setCompleted(this.completed);
        existingTask.setDueDate(this.dueDate);
        existingTask.setPriority(this.priority != null ? this.priority : existingTask.getPriority());
        return existingTask;
    }
}
