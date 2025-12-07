package com.metrifuge.LogSimulator.repository;

import com.metrifuge.LogSimulator.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Tag> searchByName(String keyword);

    @Query("SELECT t FROM Tag t LEFT JOIN t.todos td GROUP BY t ORDER BY COUNT(td) DESC")
    List<Tag> findTagsOrderedByUsage();
}
