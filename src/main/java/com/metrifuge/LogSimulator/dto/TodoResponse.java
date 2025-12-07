package com.metrifuge.LogSimulator.dto;

import com.metrifuge.LogSimulator.model.Todo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponse {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private Todo.Priority priority;
    private Todo.Category category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private String assignedTo;
    private String tags;
    private Integer estimatedHours;

    public static TodoResponse fromEntity(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.getCompleted());
        response.setPriority(todo.getPriority());
        response.setCategory(todo.getCategory());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        response.setDueDate(todo.getDueDate());
        response.setCompletedAt(todo.getCompletedAt());
        response.setAssignedTo(todo.getAssignedTo());
        response.setTags(todo.getTags());
        response.setEstimatedHours(todo.getEstimatedHours());
        return response;
    }
}
