package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.dto.TodoRequest;
import com.metrifuge.LogSimulator.dto.TodoResponse;
import com.metrifuge.LogSimulator.model.Todo;
import com.metrifuge.LogSimulator.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Todo Management", description = "APIs for managing todo items with comprehensive CRUD operations, filtering, and statistics")
public class TodoController {

    private final TodoService todoService;

    @Operation(
        summary = "Create a new todo",
        description = "Creates a new todo item with the provided details. Returns the created todo with generated ID and timestamps."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Todo created successfully",
            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @Parameter(description = "Todo details to create", required = true)
            @Valid @RequestBody TodoRequest request) {
        log.info("POST /api/todos - Creating new todo");
        log.debug("Request body: {}", request);

        long startTime = System.currentTimeMillis();
        TodoResponse response = todoService.createTodo(request);
        long duration = System.currentTimeMillis() - startTime;

        log.info("POST /api/todos - Todo created successfully with ID: {} in {}ms", response.getId(), duration);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get todo by ID",
        description = "Retrieves a specific todo item by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo found",
            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(
            @Parameter(description = "ID of the todo to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/todos/{} - Fetching todo", id);

        long startTime = System.currentTimeMillis();
        TodoResponse response = todoService.getTodoById(id);
        long duration = System.currentTimeMillis() - startTime;

        log.info("GET /api/todos/{} - Todo fetched successfully in {}ms", id, duration);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all todos with optional filtering",
        description = "Retrieves all todos with optional filters for completion status, priority, category, or keyword search. " +
                     "If no filters are provided, returns all todos. Only one filter can be applied at a time."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todos retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAllTodos(
            @Parameter(description = "Filter by completion status", example = "true")
            @RequestParam(required = false) Boolean completed,
            @Parameter(description = "Filter by priority level", example = "HIGH")
            @RequestParam(required = false) Todo.Priority priority,
            @Parameter(description = "Filter by category", example = "WORK")
            @RequestParam(required = false) Todo.Category category,
            @Parameter(description = "Search by keyword in title or description", example = "bug")
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

    @Operation(
        summary = "Update a todo",
        description = "Updates an existing todo with the provided details. All fields are updated, including completion status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo updated successfully",
            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @Parameter(description = "ID of the todo to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated todo details", required = true)
            @Valid @RequestBody TodoRequest request) {

        log.info("PUT /api/todos/{} - Updating todo", id);
        log.debug("Update request body: {}", request);

        long startTime = System.currentTimeMillis();
        TodoResponse response = todoService.updateTodo(id, request);
        long duration = System.currentTimeMillis() - startTime;

        log.info("PUT /api/todos/{} - Todo updated successfully in {}ms", id, duration);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a todo",
        description = "Permanently deletes a todo item by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Todo deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "ID of the todo to delete", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/todos/{} - Deleting todo", id);

        long startTime = System.currentTimeMillis();
        todoService.deleteTodo(id);
        long duration = System.currentTimeMillis() - startTime;

        log.info("DELETE /api/todos/{} - Todo deleted successfully in {}ms", id, duration);

        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get todo statistics",
        description = "Retrieves aggregated statistics including total todos, completed, incomplete, urgent, and high priority counts"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(
        summary = "Mark todo as complete",
        description = "Marks a todo item as completed and sets the completion timestamp"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todo marked as complete",
            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
        @ApiResponse(responseCode = "404", description = "Todo not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> completeTodo(
            @Parameter(description = "ID of the todo to mark as complete", required = true, example = "1")
            @PathVariable Long id) {
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

    @Operation(
        summary = "Health check",
        description = "Returns the health status of the Todo service"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
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
