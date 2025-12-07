package com.metrifuge.LogSimulator.service;

import com.metrifuge.LogSimulator.model.Category;
import com.metrifuge.LogSimulator.model.Tag;
import com.metrifuge.LogSimulator.model.Todo;
import com.metrifuge.LogSimulator.repository.CategoryRepository;
import com.metrifuge.LogSimulator.repository.TagRepository;
import com.metrifuge.LogSimulator.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        log.info("Fetching all todos from database");
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findAll();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos in {} ms", todos.size(), duration);
        log.debug("Todo list: {}", todos);
        return todos;
    }

    @Transactional(readOnly = true)
    public Todo getTodoById(Long id) {
        log.info("Fetching todo with id: {}", id);
        long startTime = System.currentTimeMillis();
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Todo not found with id: {}", id);
                return new RuntimeException("Todo not found with id: " + id);
            });
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved todo with id {} in {} ms", id, duration);
        log.debug("Todo details: {}", todo);
        return todo;
    }

    @Transactional
    public Todo createTodo(Todo todo) {
        log.info("Creating new todo with title: '{}'", todo.getTitle());
        log.debug("Todo creation request: {}", todo);

        if (todo.getCategory() != null && todo.getCategory().getId() != null) {
            log.debug("Fetching category with id: {}", todo.getCategory().getId());
            Category category = categoryRepository.findById(todo.getCategory().getId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", todo.getCategory().getId());
                    return new RuntimeException("Category not found");
                });
            todo.setCategory(category);
            log.debug("Associated todo with category: {}", category.getName());
        }

        if (todo.getTags() != null && !todo.getTags().isEmpty()) {
            log.debug("Processing {} tags for the todo", todo.getTags().size());
            Set<Tag> managedTags = new HashSet<>();
            for (Tag tag : todo.getTags()) {
                if (tag.getId() != null) {
                    Tag managedTag = tagRepository.findById(tag.getId())
                        .orElseThrow(() -> {
                            log.error("Tag not found with id: {}", tag.getId());
                            return new RuntimeException("Tag not found");
                        });
                    managedTags.add(managedTag);
                    log.debug("Added existing tag: {}", managedTag.getName());
                }
            }
            todo.setTags(managedTags);
        }

        long startTime = System.currentTimeMillis();
        Todo savedTodo = todoRepository.save(todo);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully created todo with id: {} in {} ms", savedTodo.getId(), duration);
        log.debug("Created todo details: {}", savedTodo);
        return savedTodo;
    }

    @Transactional
    public Todo updateTodo(Long id, Todo todoDetails) {
        log.info("Updating todo with id: {}", id);
        log.debug("Update request: {}", todoDetails);

        Todo todo = getTodoById(id);
        log.debug("Found existing todo: {}", todo);

        String oldTitle = todo.getTitle();
        Boolean wasCompleted = todo.getCompleted();

        todo.setTitle(todoDetails.getTitle());
        todo.setDescription(todoDetails.getDescription());
        todo.setCompleted(todoDetails.getCompleted());
        todo.setPriority(todoDetails.getPriority());
        todo.setStatus(todoDetails.getStatus());
        todo.setDueDate(todoDetails.getDueDate());
        todo.setEstimatedHours(todoDetails.getEstimatedHours());
        todo.setActualHours(todoDetails.getActualHours());
        todo.setAssignedTo(todoDetails.getAssignedTo());

        if (todoDetails.getCategory() != null && todoDetails.getCategory().getId() != null) {
            log.debug("Updating category to id: {}", todoDetails.getCategory().getId());
            Category category = categoryRepository.findById(todoDetails.getCategory().getId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", todoDetails.getCategory().getId());
                    return new RuntimeException("Category not found");
                });
            todo.setCategory(category);
            log.debug("Updated category to: {}", category.getName());
        } else {
            todo.setCategory(null);
            log.debug("Removed category association");
        }

        if (todoDetails.getTags() != null) {
            log.debug("Updating tags, new count: {}", todoDetails.getTags().size());
            Set<Tag> managedTags = new HashSet<>();
            for (Tag tag : todoDetails.getTags()) {
                if (tag.getId() != null) {
                    Tag managedTag = tagRepository.findById(tag.getId())
                        .orElseThrow(() -> {
                            log.error("Tag not found with id: {}", tag.getId());
                            return new RuntimeException("Tag not found");
                        });
                    managedTags.add(managedTag);
                }
            }
            todo.setTags(managedTags);
        }

        long startTime = System.currentTimeMillis();
        Todo updatedTodo = todoRepository.save(todo);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully updated todo {} in {} ms", id, duration);
        if (!oldTitle.equals(updatedTodo.getTitle())) {
            log.info("Todo title changed from '{}' to '{}'", oldTitle, updatedTodo.getTitle());
        }
        if (!wasCompleted && updatedTodo.getCompleted()) {
            log.info("Todo {} marked as completed", id);
        } else if (wasCompleted && !updatedTodo.getCompleted()) {
            log.info("Todo {} marked as incomplete", id);
        }
        log.debug("Updated todo details: {}", updatedTodo);

        return updatedTodo;
    }

    @Transactional
    public void deleteTodo(Long id) {
        log.info("Deleting todo with id: {}", id);
        Todo todo = getTodoById(id);
        log.debug("Todo to delete: {}", todo);

        long startTime = System.currentTimeMillis();
        todoRepository.delete(todo);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully deleted todo {} in {} ms", id, duration);
        log.debug("Deleted todo had title: '{}'", todo.getTitle());
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosByCompleted(Boolean completed) {
        log.info("Fetching todos with completed status: {}", completed);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByCompleted(completed);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} {} todos in {} ms", todos.size(), completed ? "completed" : "incomplete", duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosByPriority(Todo.Priority priority) {
        log.info("Fetching todos with priority: {}", priority);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByPriority(priority);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos with priority {} in {} ms", todos.size(), priority, duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosByStatus(Todo.Status status) {
        log.info("Fetching todos with status: {}", status);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByStatus(status);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos with status {} in {} ms", todos.size(), status, duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosByCategoryId(Long categoryId) {
        log.info("Fetching todos for category id: {}", categoryId);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByCategoryId(categoryId);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos for category {} in {} ms", todos.size(), categoryId, duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosByTagId(Long tagId) {
        log.info("Fetching todos for tag id: {}", tagId);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByTagId(tagId);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos for tag {} in {} ms", todos.size(), tagId, duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> searchTodos(String keyword) {
        log.info("Searching todos with keyword: '{}'", keyword);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.searchByKeyword(keyword);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Search returned {} todos in {} ms", todos.size(), duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosByAssignedTo(String assignedTo) {
        log.info("Fetching todos assigned to: {}", assignedTo);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByAssignedTo(assignedTo);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos assigned to {} in {} ms", todos.size(), assignedTo, duration);
        return todos;
    }

    @Transactional(readOnly = true)
    public List<Todo> getTodosDueBetween(LocalDateTime start, LocalDateTime end) {
        log.info("Fetching todos due between {} and {}", start, end);
        long startTime = System.currentTimeMillis();
        List<Todo> todos = todoRepository.findByDueDateBetween(start, end);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} todos due in date range in {} ms", todos.size(), duration);
        return todos;
    }
}
