package com.metrifuge.LogSimulator.config;

import com.metrifuge.LogSimulator.model.Todo;
import com.metrifuge.LogSimulator.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository todoRepository;

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("Starting Data Initialization");
        log.info("========================================");

        long count = todoRepository.count();
        log.info("Current number of todos in database: {}", count);

        if (count == 0) {
            log.info("Database is empty. Initializing with sample data...");
            initializeSampleData();
        } else {
            log.info("Database already contains {} todos. Skipping initialization.", count);
        }

        log.info("========================================");
        log.info("Data Initialization Complete");
        log.info("========================================");
    }

    private void initializeSampleData() {
        List<Todo> sampleTodos = Arrays.asList(
            createTodo("Setup development environment", "Install all necessary tools and dependencies",
                    Todo.Priority.HIGH, Todo.Category.WORK, "alice@example.com", "setup,devops", 4),

            createTodo("Write API documentation", "Document all REST endpoints with examples",
                    Todo.Priority.MEDIUM, Todo.Category.WORK, "bob@example.com", "documentation,api", 8),

            createTodo("Fix login bug", "Users unable to login with special characters in password",
                    Todo.Priority.URGENT, Todo.Category.WORK, "charlie@example.com", "bug,security", 2),

            createTodo("Code review for PR #123", "Review and provide feedback on authentication changes",
                    Todo.Priority.HIGH, Todo.Category.WORK, "diana@example.com", "review,code-quality", 1),

            createTodo("Database migration", "Migrate user data to new schema",
                    Todo.Priority.URGENT, Todo.Category.WORK, "alice@example.com", "database,migration", 6),

            createTodo("Update dependencies", "Update all outdated npm and maven packages",
                    Todo.Priority.LOW, Todo.Category.WORK, "bob@example.com", "maintenance,dependencies", 3),

            createTodo("Implement caching layer", "Add Redis caching for frequently accessed data",
                    Todo.Priority.MEDIUM, Todo.Category.WORK, "charlie@example.com", "feature,performance", 12),

            createTodo("Security audit", "Conduct comprehensive security audit of the application",
                    Todo.Priority.HIGH, Todo.Category.WORK, "diana@example.com", "security,audit", 16),

            createTodo("Write unit tests", "Increase test coverage to 80%",
                    Todo.Priority.MEDIUM, Todo.Category.WORK, "alice@example.com", "testing,quality", 10),

            createTodo("Deploy to staging", "Deploy version 2.1 to staging environment",
                    Todo.Priority.HIGH, Todo.Category.WORK, "bob@example.com", "deployment,devops", 2),

            createTodo("Grocery shopping", "Buy milk, eggs, bread, and vegetables",
                    Todo.Priority.LOW, Todo.Category.SHOPPING, "alice@example.com", "groceries,personal", 1),

            createTodo("Doctor appointment", "Annual checkup appointment",
                    Todo.Priority.MEDIUM, Todo.Category.HEALTH, "alice@example.com", "health,appointment", 2),

            createTodo("Complete online course", "Finish Kubernetes certification course",
                    Todo.Priority.MEDIUM, Todo.Category.EDUCATION, "charlie@example.com", "learning,certification", 20),

            createTodo("Pay utility bills", "Pay electricity and water bills",
                    Todo.Priority.HIGH, Todo.Category.FINANCE, "bob@example.com", "bills,finance", 1),

            createTodo("Team standup meeting", "Daily standup with the development team",
                    Todo.Priority.MEDIUM, Todo.Category.WORK, "diana@example.com", "meeting,team", 1)
        );

        log.info("Creating {} sample todos...", sampleTodos.size());

        for (int i = 0; i < sampleTodos.size(); i++) {
            Todo todo = sampleTodos.get(i);
            todoRepository.save(todo);
            log.debug("Created sample todo {}/{}: '{}' (Priority: {}, Category: {})",
                    i + 1, sampleTodos.size(), todo.getTitle(), todo.getPriority(), todo.getCategory());
        }

        log.info("Successfully created {} sample todos", sampleTodos.size());

        // Mark some as completed
        List<Todo> allTodos = todoRepository.findAll();
        int completedCount = 0;
        for (int i = 0; i < Math.min(5, allTodos.size()); i++) {
            Todo todo = allTodos.get(i);
            todo.setCompleted(true);
            todo.setCompletedAt(LocalDateTime.now().minusDays(i));
            todoRepository.save(todo);
            completedCount++;
            log.debug("Marked todo '{}' as completed", todo.getTitle());
        }

        log.info("Marked {} todos as completed", completedCount);

        // Log statistics
        long total = todoRepository.count();
        long completed = todoRepository.countByCompleted(true);
        long urgent = todoRepository.countByPriority(Todo.Priority.URGENT);
        long high = todoRepository.countByPriority(Todo.Priority.HIGH);

        log.info("Database Statistics - Total: {}, Completed: {}, Incomplete: {}, Urgent: {}, High Priority: {}",
                total, completed, total - completed, urgent, high);
    }

    private Todo createTodo(String title, String description, Todo.Priority priority,
                           Todo.Category category, String assignedTo, String tags, int estimatedHours) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setPriority(priority);
        todo.setCategory(category);
        todo.setAssignedTo(assignedTo);
        todo.setTags(tags);
        todo.setEstimatedHours(estimatedHours);
        todo.setCompleted(false);
        todo.setDueDate(LocalDateTime.now().plusDays((long) (Math.random() * 30)));
        return todo;
    }
}
