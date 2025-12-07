package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.dto.TodoRequest;
import com.metrifuge.LogSimulator.dto.TodoResponse;
import com.metrifuge.LogSimulator.model.Todo;
import com.metrifuge.LogSimulator.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        log.info("POST /api/todos - Creating new todo");
        log.debug("Request body: {}", request);

        long startTime = System.currentTimeMillis();
        TodoResponse response = todoService.createTodo(request);
        long duration = System.currentTimeMillis() - startTime;

        log.info("POST /api/todos - Todo created successfully with ID: {} in {}ms", response.getId(), duration);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long id) {
        log.info("GET /api/todos/{} - Fetching todo", id);

        long startTime = System.currentTimeMillis();
        TodoResponse response = todoService.getTodoById(id);
        long duration = System.currentTimeMillis() - startTime;

        log.info("GET /api/todos/{} - Todo fetched successfully in {}ms", id, duration);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Todo.Priority priority,
            @RequestParam(required = false) Todo.Category category,
            @RequestParam(required = false) String search) {

        log.info("GET /api/todos - Fetching todos with filters - completed: {}, priority: {}, category: {}, search: '{}'",
                completed, priority, category, search);

        long startTime = System.currentTimeMillis();
        List<TodoResponse> response;

        if (search != null && !search.isEmpty()) {
            log.debug("Performing search with keyword: '{}'", search);
            response = todoService.searchTodos(search);
        } else if (completed != null) {
            log.debug("Filtering by completed status: {}", completed);
            response = todoService.getTodosByCompleted(completed);
        } else if (priority != null) {
            log.debug("Filtering by priority: {}", priority);
            response = todoService.getTodosByPriority(priority);
        } else if (category != null) {
            log.debug("Filtering by category: {}", category);
            response = todoService.getTodosByCategory(category);
        } else {
            log.debug("Fetching all todos without filters");
            response = todoService.getAllTodos();
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("GET /api/todos - Returned {} todos in {}ms", response.size(), duration);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request) {

        log.info("PUT /api/todos/{} - Updating todo", id);
        log.debug("Update request body: {}", request);

        long startTime = System.currentTimeMillis();
        TodoResponse response = todoService.updateTodo(id, request);
        long duration = System.currentTimeMillis() - startTime;

        log.info("PUT /api/todos/{} - Todo updated successfully in {}ms", id, duration);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        log.info("DELETE /api/todos/{} - Deleting todo", id);

        long startTime = System.currentTimeMillis();
        todoService.deleteTodo(id);
        long duration = System.currentTimeMillis() - startTime;

        log.info("DELETE /api/todos/{} - Todo deleted successfully in {}ms", id, duration);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        log.info("GET /api/todos/statistics - Fetching statistics");

        long startTime = System.currentTimeMillis();
        Map<String, Long> stats = todoService.getStatistics();
        long duration = System.currentTimeMillis() - startTime;

        log.info("GET /api/todos/statistics - Statistics calculated in {}ms", duration);
        log.debug("Statistics: {}", stats);

        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> completeTodo(@PathVariable Long id) {
        log.info("PATCH /api/todos/{}/complete - Marking todo as complete", id);

        long startTime = System.currentTimeMillis();
        TodoResponse todo = todoService.getTodoById(id);

        TodoRequest updateRequest = new TodoRequest();
        updateRequest.setTitle(todo.getTitle());
        updateRequest.setDescription(todo.getDescription());
        updateRequest.setCompleted(true);
        updateRequest.setPriority(todo.getPriority());
        updateRequest.setCategory(todo.getCategory());
        updateRequest.setDueDate(todo.getDueDate());
        updateRequest.setAssignedTo(todo.getAssignedTo());
        updateRequest.setTags(todo.getTags());
        updateRequest.setEstimatedHours(todo.getEstimatedHours());

        TodoResponse response = todoService.updateTodo(id, updateRequest);
        long duration = System.currentTimeMillis() - startTime;

        log.info("PATCH /api/todos/{}/complete - Todo marked as complete in {}ms", id, duration);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.debug("GET /api/todos/health - Health check");

        Map<String, String> health = Map.of(
                "status", "UP",
                "service", "TodoService",
                "timestamp", java.time.LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(health);
    }
}
