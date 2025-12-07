package com.metrifuge.LogSimulator.service;

import com.metrifuge.LogSimulator.dto.TodoRequest;
import com.metrifuge.LogSimulator.dto.TodoResponse;
import com.metrifuge.LogSimulator.exception.TodoNotFoundException;
import com.metrifuge.LogSimulator.model.Todo;
import com.metrifuge.LogSimulator.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        log.info("Creating new todo with title: '{}'", request.getTitle());
        log.debug("Todo details - Priority: {}, Category: {}, DueDate: {}",
                request.getPriority(), request.getCategory(), request.getDueDate());

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        todo.setPriority(request.getPriority() != null ? request.getPriority() : Todo.Priority.MEDIUM);
        todo.setCategory(request.getCategory() != null ? request.getCategory() : Todo.Category.GENERAL);
        todo.setDueDate(request.getDueDate());
        todo.setAssignedTo(request.getAssignedTo());
        todo.setTags(request.getTags());
        todo.setEstimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : 0);

        Todo savedTodo = todoRepository.save(todo);
        log.info("Successfully created todo with ID: {}", savedTodo.getId());
        log.debug("Created todo complete details: {}", savedTodo);

        return TodoResponse.fromEntity(savedTodo);
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodoById(Long id) {
        log.info("Fetching todo by ID: {}", id);

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Todo not found with ID: {}", id);
                    return new TodoNotFoundException(id);
                });

        log.debug("Found todo: {}", todo);
        return TodoResponse.fromEntity(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTodos() {
        log.info("Fetching all todos");

        List<Todo> todos = todoRepository.findAll();
        log.info("Found {} todos in database", todos.size());
        log.debug("Todo IDs: {}", todos.stream().map(Todo::getId).collect(Collectors.toList()));

        return todos.stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        log.info("Updating todo with ID: {}", id);
        log.debug("Update request details: {}", request);

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot update - Todo not found with ID: {}", id);
                    return new TodoNotFoundException(id);
                });

        log.debug("Existing todo before update: {}", todo);

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());

        if (request.getCompleted() != null) {
            boolean wasCompleted = todo.getCompleted();
            todo.setCompleted(request.getCompleted());
            if (!wasCompleted && request.getCompleted()) {
                log.info("Todo ID: {} marked as completed", id);
                todo.setCompletedAt(LocalDateTime.now());
            } else if (wasCompleted && !request.getCompleted()) {
                log.info("Todo ID: {} marked as incomplete", id);
                todo.setCompletedAt(null);
            }
        }

        if (request.getPriority() != null) {
            log.debug("Priority changed from {} to {}", todo.getPriority(), request.getPriority());
            todo.setPriority(request.getPriority());
        }

        if (request.getCategory() != null) {
            log.debug("Category changed from {} to {}", todo.getCategory(), request.getCategory());
            todo.setCategory(request.getCategory());
        }

        todo.setDueDate(request.getDueDate());
        todo.setAssignedTo(request.getAssignedTo());
        todo.setTags(request.getTags());

        if (request.getEstimatedHours() != null) {
            todo.setEstimatedHours(request.getEstimatedHours());
        }

        Todo updatedTodo = todoRepository.save(todo);
        log.info("Successfully updated todo with ID: {}", id);
        log.debug("Updated todo complete details: {}", updatedTodo);

        return TodoResponse.fromEntity(updatedTodo);
    }

    @Transactional
    public void deleteTodo(Long id) {
        log.info("Deleting todo with ID: {}", id);

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete - Todo not found with ID: {}", id);
                    return new TodoNotFoundException(id);
                });

        log.debug("Deleting todo: {}", todo);
        todoRepository.delete(todo);
        log.info("Successfully deleted todo with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByCompleted(Boolean completed) {
        log.info("Fetching todos by completed status: {}", completed);

        List<Todo> todos = todoRepository.findByCompleted(completed);
        log.info("Found {} todos with completed={}", todos.size(), completed);

        return todos.stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByPriority(Todo.Priority priority) {
        log.info("Fetching todos by priority: {}", priority);

        List<Todo> todos = todoRepository.findByPriority(priority);
        log.info("Found {} todos with priority={}", todos.size(), priority);

        return todos.stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByCategory(Todo.Category category) {
        log.info("Fetching todos by category: {}", category);

        List<Todo> todos = todoRepository.findByCategory(category);
        log.info("Found {} todos with category={}", todos.size(), category);

        return todos.stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> searchTodos(String keyword) {
        log.info("Searching todos with keyword: '{}'", keyword);

        List<Todo> todos = todoRepository.searchByKeyword(keyword);
        log.info("Found {} todos matching keyword '{}'", todos.size(), keyword);

        return todos.stream()
                .map(TodoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStatistics() {
        log.info("Calculating todo statistics");

        long totalTodos = todoRepository.count();
        long completedTodos = todoRepository.countByCompleted(true);
        long incompleteTodos = todoRepository.countByCompleted(false);

        long urgentTodos = todoRepository.countByPriority(Todo.Priority.URGENT);
        long highPriorityTodos = todoRepository.countByPriority(Todo.Priority.HIGH);

        log.info("Statistics - Total: {}, Completed: {}, Incomplete: {}, Urgent: {}, High Priority: {}",
                totalTodos, completedTodos, incompleteTodos, urgentTodos, highPriorityTodos);

        Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("total", totalTodos);
        stats.put("completed", completedTodos);
        stats.put("incomplete", incompleteTodos);
        stats.put("urgent", urgentTodos);
        stats.put("highPriority", highPriorityTodos);

        return stats;
    }
}
