package com.example.todoapp.graphql;

import com.example.todoapp.domain.enums.TaskPriority;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskUpdateInput {
    private String title;
    private String description;
    private Boolean completed;
    private LocalDateTime dueDate;
    private TaskPriority priority;
}
