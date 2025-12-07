package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.model.Category;
import com.metrifuge.LogSimulator.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all categories from the database")
    public ResponseEntity<List<Category>> getAllCategories() {
        log.info("REST request to GET all categories");
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryService.getAllCategories();
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} categories, request took {} ms", categories.size(), duration);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by its ID")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "ID of the category to retrieve") @PathVariable Long id) {
        log.info("REST request to GET category with id: {}", id);
        long startTime = System.currentTimeMillis();
        Category category = categoryService.getCategoryById(id);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning category '{}', request took {} ms", category.getName(), duration);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new category")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        log.info("REST request to POST new category with name: '{}'", category.getName());
        log.debug("Request body: {}", category);
        long startTime = System.currentTimeMillis();
        Category createdCategory = categoryService.createCategory(category);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: created category with id {}, request took {} ms", createdCategory.getId(), duration);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<Category> updateCategory(
            @Parameter(description = "ID of the category to update") @PathVariable Long id,
            @Valid @RequestBody Category category) {
        log.info("REST request to PUT update category with id: {}", id);
        log.debug("Request body: {}", category);
        long startTime = System.currentTimeMillis();
        Category updatedCategory = categoryService.updateCategory(id, category);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: updated category {}, request took {} ms", id, duration);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category by its ID")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID of the category to delete") @PathVariable Long id) {
        log.info("REST request to DELETE category with id: {}", id);
        long startTime = System.currentTimeMillis();
        categoryService.deleteCategory(id);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: deleted category {}, request took {} ms", id, duration);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Search categories by keyword")
    public ResponseEntity<List<Category>> searchCategories(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        log.info("REST request to SEARCH categories with keyword: '{}'", keyword);
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryService.searchCategories(keyword);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: search returned {} categories, request took {} ms", categories.size(), duration);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/ordered-by-usage")
    @Operation(summary = "Get categories ordered by usage", description = "Get categories ordered by number of associated todos")
    public ResponseEntity<List<Category>> getCategoriesOrderedByUsage() {
        log.info("REST request to GET categories ordered by todo count");
        long startTime = System.currentTimeMillis();
        List<Category> categories = categoryService.getCategoriesOrderedByTodoCount();
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} categories ordered by usage, request took {} ms", categories.size(), duration);
        return ResponseEntity.ok(categories);
    }
}
