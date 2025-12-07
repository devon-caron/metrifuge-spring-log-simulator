package com.metrifuge.LogSimulator.dto;

import com.metrifuge.LogSimulator.model.Todo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating a todo item")
public class TodoRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be less than 500 characters")
    @Schema(description = "Title of the todo", example = "Complete project documentation", required = true)
    private String title;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    @Schema(description = "Detailed description of the todo", example = "Write comprehensive API documentation with examples")
    private String description;

    @Schema(description = "Completion status", example = "false", defaultValue = "false")
    private Boolean completed;

    @Schema(description = "Priority level", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    private Todo.Priority priority;

    @Schema(description = "Category", example = "WORK", allowableValues = {"GENERAL", "WORK", "PERSONAL", "SHOPPING", "HEALTH", "EDUCATION", "FINANCE", "OTHER"})
    private Todo.Category category;

    @Schema(description = "Due date and time", example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Size(max = 100, message = "AssignedTo must be less than 100 characters")
    @Schema(description = "Email of the person assigned to this todo", example = "alice@example.com")
    private String assignedTo;

    @Size(max = 50, message = "Tags must be less than 50 characters")
    @Schema(description = "Comma-separated tags", example = "documentation,api,urgent")
    private String tags;

    @Schema(description = "Estimated hours to complete", example = "8", minimum = "0")
    private Integer estimatedHours;
}
