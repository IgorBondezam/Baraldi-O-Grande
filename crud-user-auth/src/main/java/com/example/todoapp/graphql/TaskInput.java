package com.example.todoapp.graphql;

import com.example.todoapp.domain.enums.TaskPriority;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskInput {
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
    private TaskPriority priority;
}
