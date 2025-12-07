package com.metrifuge.LogSimulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private Long id;
    private String title;
    private String description;
    private Boolean completed = false;
    private Priority priority = Priority.MEDIUM;
    private Category category = Category.GENERAL;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private String assignedTo;
    private String tags;
    private Integer estimatedHours = 0;

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum Category {
        GENERAL, WORK, PERSONAL, SHOPPING, HEALTH, EDUCATION, FINANCE, OTHER
    }
}
