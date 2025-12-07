package com.metrifuge.LogSimulator.dto;

import com.metrifuge.LogSimulator.model.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be less than 500 characters")
    private String title;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    private Boolean completed;

    private Todo.Priority priority;

    private Todo.Category category;

    private LocalDateTime dueDate;

    @Size(max = 100, message = "AssignedTo must be less than 100 characters")
    private String assignedTo;

    @Size(max = 50, message = "Tags must be less than 50 characters")
    private String tags;

    private Integer estimatedHours;
}
