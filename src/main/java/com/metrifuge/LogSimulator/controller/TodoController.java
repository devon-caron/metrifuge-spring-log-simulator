package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.model.Todo;
import com.metrifuge.LogSimulator.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Todo", description = "Todo management APIs")
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    @Operation(summary = "Get all todos", description = "Retrieve all todos from the database")
    public ResponseEntity<List<Todo>> getAllTodos() {
        log.info("REST request to GET all todos");
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getAllTodos();
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos, request took {} ms", todos.size(), duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get todo by ID", description = "Retrieve a specific todo by its ID")
    public ResponseEntity<Todo> getTodoById(
            @Parameter(description = "ID of the todo to retrieve") @PathVariable Long id) {
        log.info("REST request to GET todo with id: {}", id);
        long startTime = System.currentTimeMillis();
        Todo todo = todoService.getTodoById(id);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning todo {}, request took {} ms", id, duration);
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    @Operation(summary = "Create new todo", description = "Create a new todo item")
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        log.info("REST request to POST new todo with title: '{}'", todo.getTitle());
        log.debug("Request body: {}", todo);
        long startTime = System.currentTimeMillis();
        Todo createdTodo = todoService.createTodo(todo);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: created todo with id {}, request took {} ms", createdTodo.getId(), duration);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update todo", description = "Update an existing todo")
    public ResponseEntity<Todo> updateTodo(
            @Parameter(description = "ID of the todo to update") @PathVariable Long id,
            @Valid @RequestBody Todo todo) {
        log.info("REST request to PUT update todo with id: {}", id);
        log.debug("Request body: {}", todo);
        long startTime = System.currentTimeMillis();
        Todo updatedTodo = todoService.updateTodo(id, todo);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: updated todo {}, request took {} ms", id, duration);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete todo", description = "Delete a todo by its ID")
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "ID of the todo to delete") @PathVariable Long id) {
        log.info("REST request to DELETE todo with id: {}", id);
        long startTime = System.currentTimeMillis();
        todoService.deleteTodo(id);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: deleted todo {}, request took {} ms", id, duration);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/completed/{completed}")
    @Operation(summary = "Get todos by completion status", description = "Filter todos by completed or incomplete status")
    public ResponseEntity<List<Todo>> getTodosByCompleted(
            @Parameter(description = "Completion status") @PathVariable Boolean completed) {
        log.info("REST request to GET todos with completed: {}", completed);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosByCompleted(completed);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} {} todos, request took {} ms",
            todos.size(), completed ? "completed" : "incomplete", duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get todos by priority", description = "Filter todos by priority level")
    public ResponseEntity<List<Todo>> getTodosByPriority(
            @Parameter(description = "Priority level (LOW, MEDIUM, HIGH, URGENT)") @PathVariable Todo.Priority priority) {
        log.info("REST request to GET todos with priority: {}", priority);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosByPriority(priority);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos with priority {}, request took {} ms",
            todos.size(), priority, duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get todos by status", description = "Filter todos by workflow status")
    public ResponseEntity<List<Todo>> getTodosByStatus(
            @Parameter(description = "Status (TODO, IN_PROGRESS, BLOCKED, REVIEW, DONE)") @PathVariable Todo.Status status) {
        log.info("REST request to GET todos with status: {}", status);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosByStatus(status);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos with status {}, request took {} ms",
            todos.size(), status, duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get todos by category", description = "Filter todos by category ID")
    public ResponseEntity<List<Todo>> getTodosByCategoryId(
            @Parameter(description = "Category ID") @PathVariable Long categoryId) {
        log.info("REST request to GET todos for category: {}", categoryId);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosByCategoryId(categoryId);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos for category {}, request took {} ms",
            todos.size(), categoryId, duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/tag/{tagId}")
    @Operation(summary = "Get todos by tag", description = "Filter todos by tag ID")
    public ResponseEntity<List<Todo>> getTodosByTagId(
            @Parameter(description = "Tag ID") @PathVariable Long tagId) {
        log.info("REST request to GET todos for tag: {}", tagId);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosByTagId(tagId);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos for tag {}, request took {} ms",
            todos.size(), tagId, duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    @Operation(summary = "Search todos", description = "Search todos by keyword in title or description")
    public ResponseEntity<List<Todo>> searchTodos(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        log.info("REST request to SEARCH todos with keyword: '{}'", keyword);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.searchTodos(keyword);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: search returned {} todos, request took {} ms", todos.size(), duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/assigned/{assignedTo}")
    @Operation(summary = "Get todos by assignee", description = "Filter todos by assigned user")
    public ResponseEntity<List<Todo>> getTodosByAssignedTo(
            @Parameter(description = "Username of assignee") @PathVariable String assignedTo) {
        log.info("REST request to GET todos assigned to: {}", assignedTo);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosByAssignedTo(assignedTo);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos assigned to {}, request took {} ms",
            todos.size(), assignedTo, duration);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/due")
    @Operation(summary = "Get todos due in date range", description = "Filter todos by due date range")
    public ResponseEntity<List<Todo>> getTodosDueBetween(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to GET todos due between {} and {}", start, end);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoService.getTodosDueBetween(start, end);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} todos in date range, request took {} ms", todos.size(), duration);
        return ResponseEntity.ok(todos);
    }
}
