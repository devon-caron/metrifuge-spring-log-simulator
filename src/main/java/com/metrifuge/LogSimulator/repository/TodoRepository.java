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

    List<Todo> findByStatus(Todo.Status status);

    List<Todo> findByCategoryId(Long categoryId);

    List<Todo> findByAssignedTo(String assignedTo);

    @Query("SELECT t FROM Todo t WHERE t.dueDate BETWEEN :start AND :end")
    List<Todo> findByDueDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Todo t JOIN t.tags tag WHERE tag.id = :tagId")
    List<Todo> findByTagId(@Param("tagId") Long tagId);

    @Query("SELECT t FROM Todo t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Todo> searchByKeyword(@Param("keyword") String keyword);

    List<Todo> findByCompletedAndPriority(Boolean completed, Todo.Priority priority);

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.completed = true")
    Long countCompleted();

    @Query("SELECT COUNT(t) FROM Todo t WHERE t.completed = false")
    Long countIncomplete();
}
