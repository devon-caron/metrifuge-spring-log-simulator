package com.metrifuge.LogSimulator.dto;

import com.metrifuge.LogSimulator.model.Todo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing todo item details")
public class TodoResponse {

    @Schema(description = "Unique identifier of the todo", example = "1")
    private Long id;

    @Schema(description = "Title of the todo", example = "Complete project documentation")
    private String title;

    @Schema(description = "Detailed description of the todo", example = "Write comprehensive API documentation with examples")
    private String description;

    @Schema(description = "Completion status", example = "false")
    private Boolean completed;

    @Schema(description = "Priority level", example = "HIGH")
    private Todo.Priority priority;

    @Schema(description = "Category", example = "WORK")
    private Todo.Category category;

    @Schema(description = "Timestamp when the todo was created", example = "2025-12-07T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the todo was last updated", example = "2025-12-07T15:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Due date and time", example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Timestamp when the todo was completed", example = "2025-12-07T16:00:00")
    private LocalDateTime completedAt;

    @Schema(description = "Email of the person assigned to this todo", example = "alice@example.com")
    private String assignedTo;

    @Schema(description = "Comma-separated tags", example = "documentation,api,urgent")
    private String tags;

    @Schema(description = "Estimated hours to complete", example = "8")
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
