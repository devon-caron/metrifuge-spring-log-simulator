package com.metrifuge.LogSimulator.service;

import com.metrifuge.LogSimulator.model.Category;
import com.metrifuge.LogSimulator.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("Fetching all categories from database");
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryRepository.findAll();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} categories in {} ms", categories.size(), duration);
        log.debug("Categories: {}", categories);
        return categories;
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);
        long startTime = System.currentTimeMillis();
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Category not found with id: {}", id);
                return new RuntimeException("Category not found with id: " + id);
            });
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved category '{}' in {} ms", category.getName(), duration);
        log.debug("Category details: {}", category);
        return category;
    }

    @Transactional
    public Category createCategory(Category category) {
        log.info("Creating new category with name: '{}'", category.getName());
        log.debug("Category creation request: {}", category);

        categoryRepository.findByName(category.getName()).ifPresent(existing -> {
            log.error("Category with name '{}' already exists", category.getName());
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        });

        long startTime = System.currentTimeMillis();
        Category savedCategory = categoryRepository.save(category);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully created category with id: {} in {} ms", savedCategory.getId(), duration);
        log.debug("Created category details: {}", savedCategory);
        return savedCategory;
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        log.info("Updating category with id: {}", id);
        log.debug("Update request: {}", categoryDetails);

        Category category = getCategoryById(id);
        log.debug("Found existing category: {}", category);

        String oldName = category.getName();

        if (!category.getName().equals(categoryDetails.getName())) {
            categoryRepository.findByName(categoryDetails.getName()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    log.error("Category with name '{}' already exists", categoryDetails.getName());
                    throw new RuntimeException("Category with name '" + categoryDetails.getName() + "' already exists");
                }
            });
        }

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setColor(categoryDetails.getColor());

        long startTime = System.currentTimeMillis();
        Category updatedCategory = categoryRepository.save(category);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully updated category {} in {} ms", id, duration);
        if (!oldName.equals(updatedCategory.getName())) {
            log.info("Category name changed from '{}' to '{}'", oldName, updatedCategory.getName());
        }
        log.debug("Updated category details: {}", updatedCategory);

        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        Category category = getCategoryById(id);
        log.debug("Category to delete: {}", category);

        if (!category.getTodos().isEmpty()) {
            log.warn("Deleting category '{}' with {} associated todos", category.getName(), category.getTodos().size());
        }

        long startTime = System.currentTimeMillis();
        categoryRepository.delete(category);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully deleted category {} in {} ms", id, duration);
        log.debug("Deleted category had name: '{}'", category.getName());
    }

    @Transactional(readOnly = true)
    public List<Category> searchCategories(String keyword) {
        log.info("Searching categories with keyword: '{}'", keyword);
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryRepository.searchByName(keyword);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Search returned {} categories in {} ms", categories.size(), duration);
        return categories;
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesOrderedByTodoCount() {
        log.info("Fetching categories ordered by todo count");
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryRepository.findCategoriesOrderedByTodoCount();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} categories ordered by usage in {} ms", categories.size(), duration);
        return categories;
    }
}
