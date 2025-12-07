package com.metrifuge.LogSimulator.repository;

import com.metrifuge.LogSimulator.model.Todo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TodoRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Todo> todoRowMapper = new RowMapper<Todo>() {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Todo todo = new Todo();
            todo.setId(rs.getLong("id"));
            todo.setTitle(rs.getString("title"));
            todo.setDescription(rs.getString("description"));
            todo.setCompleted(rs.getBoolean("completed"));
            todo.setPriority(Todo.Priority.valueOf(rs.getString("priority")));
            todo.setCategory(Todo.Category.valueOf(rs.getString("category")));

            Timestamp createdAt = rs.getTimestamp("created_at");
            todo.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            todo.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

            Timestamp dueDate = rs.getTimestamp("due_date");
            todo.setDueDate(dueDate != null ? dueDate.toLocalDateTime() : null);

            Timestamp completedAt = rs.getTimestamp("completed_at");
            todo.setCompletedAt(completedAt != null ? completedAt.toLocalDateTime() : null);

            todo.setAssignedTo(rs.getString("assigned_to"));
            todo.setTags(rs.getString("tags"));
            todo.setEstimatedHours(rs.getInt("estimated_hours"));

            return todo;
        }
    };

    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            return insert(todo);
        } else {
            return update(todo);
        }
    }

    private Todo insert(Todo todo) {
        String sql = "INSERT INTO todos (title, description, completed, priority, category, created_at, updated_at, " +
                     "due_date, completed_at, assigned_to, tags, estimated_hours) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        todo.setCreatedAt(now);
        todo.setUpdatedAt(now);

        jdbcTemplate.update(sql,
            todo.getTitle(),
            todo.getDescription(),
            todo.getCompleted(),
            todo.getPriority().name(),
            todo.getCategory().name(),
            Timestamp.valueOf(todo.getCreatedAt()),
            Timestamp.valueOf(todo.getUpdatedAt()),
            todo.getDueDate() != null ? Timestamp.valueOf(todo.getDueDate()) : null,
            todo.getCompletedAt() != null ? Timestamp.valueOf(todo.getCompletedAt()) : null,
            todo.getAssignedTo(),
            todo.getTags(),
            todo.getEstimatedHours()
        );

        // SQLite-compatible way to get the last inserted ID
        Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
        todo.setId(id);
        return todo;
    }

    private Todo update(Todo todo) {
        String sql = "UPDATE todos SET title = ?, description = ?, completed = ?, priority = ?, category = ?, " +
                     "updated_at = ?, due_date = ?, completed_at = ?, assigned_to = ?, tags = ?, estimated_hours = ? " +
                     "WHERE id = ?";

        todo.setUpdatedAt(LocalDateTime.now());

        jdbcTemplate.update(sql,
            todo.getTitle(),
            todo.getDescription(),
            todo.getCompleted(),
            todo.getPriority().name(),
            todo.getCategory().name(),
            Timestamp.valueOf(todo.getUpdatedAt()),
            todo.getDueDate() != null ? Timestamp.valueOf(todo.getDueDate()) : null,
            todo.getCompletedAt() != null ? Timestamp.valueOf(todo.getCompletedAt()) : null,
            todo.getAssignedTo(),
            todo.getTags(),
            todo.getEstimatedHours(),
            todo.getId()
        );

        return todo;
    }

    public Optional<Todo> findById(Long id) {
        String sql = "SELECT * FROM todos WHERE id = ?";
        List<Todo> todos = jdbcTemplate.query(sql, todoRowMapper, id);
        return todos.isEmpty() ? Optional.empty() : Optional.of(todos.get(0));
    }

    public List<Todo> findAll() {
        String sql = "SELECT * FROM todos ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper);
    }

    public void delete(Todo todo) {
        deleteById(todo.getId());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM todos WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Todo> findByCompleted(Boolean completed) {
        String sql = "SELECT * FROM todos WHERE completed = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper, completed);
    }

    public List<Todo> findByPriority(Todo.Priority priority) {
        String sql = "SELECT * FROM todos WHERE priority = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper, priority.name());
    }

    public List<Todo> findByCategory(Todo.Category category) {
        String sql = "SELECT * FROM todos WHERE category = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper, category.name());
    }

    public List<Todo> findByAssignedTo(String assignedTo) {
        String sql = "SELECT * FROM todos WHERE assigned_to = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper, assignedTo);
    }

    public List<Todo> findByCompletedAndPriority(Boolean completed, Todo.Priority priority) {
        String sql = "SELECT * FROM todos WHERE completed = ? AND priority = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper, completed, priority.name());
    }

    public List<Todo> findByDueDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM todos WHERE due_date BETWEEN ? AND ? ORDER BY due_date ASC";
        return jdbcTemplate.query(sql, todoRowMapper, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<Todo> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM todos WHERE title LIKE ? OR description LIKE ? ORDER BY created_at DESC";
        String searchPattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, todoRowMapper, searchPattern, searchPattern);
    }

    public List<Todo> findByCreatedAtAfter(LocalDateTime date) {
        String sql = "SELECT * FROM todos WHERE created_at > ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, todoRowMapper, Timestamp.valueOf(date));
    }

    public Long count() {
        String sql = "SELECT COUNT(*) FROM todos";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Long countByCompleted(Boolean completed) {
        String sql = "SELECT COUNT(*) FROM todos WHERE completed = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, completed);
    }

    public Long countByPriority(Todo.Priority priority) {
        String sql = "SELECT COUNT(*) FROM todos WHERE priority = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, priority.name());
    }

    public Long countByCategory(Todo.Category category) {
        String sql = "SELECT COUNT(*) FROM todos WHERE category = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, category.name());
    }
}
