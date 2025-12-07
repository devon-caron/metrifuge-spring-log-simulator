package com.metrifuge.LogSimulator.repository;

import com.metrifuge.LogSimulator.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByName(String keyword);

    @Query("SELECT c FROM Category c LEFT JOIN c.todos t GROUP BY c ORDER BY COUNT(t) DESC")
    List<Category> findCategoriesOrderedByTodoCount();
}
