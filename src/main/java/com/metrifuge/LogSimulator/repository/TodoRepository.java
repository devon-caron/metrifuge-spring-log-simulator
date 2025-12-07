package com.metrifuge.LogSimulator.repository;

import com.metrifuge.LogSimulator.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByCompleted(Boolean completed);

    List<Todo> findByPriority(Todo.Priority priority);

    List<Todo> findByCategory(Todo.Category category);

    List<Todo> findByAssignedTo(String assignedTo);

    List<Todo> findByCompletedAndPriority(Boolean completed, Todo.Priority priority);

    @Query("SELECT t FROM Todo t WHERE t.dueDate BETWEEN :start AND :end")
    List<Todo> findByDueDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Todo t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword%")
    List<Todo> searchByKeyword(@Param("keyword") String keyword);

    List<Todo> findByCreatedAtAfter(LocalDateTime date);

    Long countByCompleted(Boolean completed);

    Long countByPriority(Todo.Priority priority);

    Long countByCategory(Todo.Category category);
}
