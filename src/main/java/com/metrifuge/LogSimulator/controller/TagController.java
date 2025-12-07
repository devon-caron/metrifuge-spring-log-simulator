package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.model.Tag;
import com.metrifuge.LogSimulator.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Tag management APIs")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Get all tags", description = "Retrieve all tags from the database")
    public ResponseEntity<List<Tag>> getAllTags() {
        log.info("REST request to GET all tags");
        long startTime = System.currentTimeMillis();
        List<Tag> tags = tagService.getAllTags();
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} tags, request took {} ms", tags.size(), duration);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Retrieve a specific tag by its ID")
    public ResponseEntity<Tag> getTagById(
            @Parameter(description = "ID of the tag to retrieve") @PathVariable Long id) {
        log.info("REST request to GET tag with id: {}", id);
        long startTime = System.currentTimeMillis();
        Tag tag = tagService.getTagById(id);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning tag '{}', request took {} ms", tag.getName(), duration);
        return ResponseEntity.ok(tag);
    }

    @PostMapping
    @Operation(summary = "Create new tag", description = "Create a new tag")
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) {
        log.info("REST request to POST new tag with name: '{}'", tag.getName());
        log.debug("Request body: {}", tag);
        long startTime = System.currentTimeMillis();
        Tag createdTag = tagService.createTag(tag);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: created tag with id {}, request took {} ms", createdTag.getId(), duration);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tag", description = "Update an existing tag")
    public ResponseEntity<Tag> updateTag(
            @Parameter(description = "ID of the tag to update") @PathVariable Long id,
            @Valid @RequestBody Tag tag) {
        log.info("REST request to PUT update tag with id: {}", id);
        log.debug("Request body: {}", tag);
        long startTime = System.currentTimeMillis();
        Tag updatedTag = tagService.updateTag(id, tag);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: updated tag {}, request took {} ms", id, duration);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag", description = "Delete a tag by its ID")
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "ID of the tag to delete") @PathVariable Long id) {
        log.info("REST request to DELETE tag with id: {}", id);
        long startTime = System.currentTimeMillis();
        tagService.deleteTag(id);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: deleted tag {}, request took {} ms", id, duration);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search tags", description = "Search tags by keyword")
    public ResponseEntity<List<Tag>> searchTags(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        log.info("REST request to SEARCH tags with keyword: '{}'", keyword);
        long startTime = System.currentTimeMillis();
        List<Tag> tags = tagService.searchTags(keyword);
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: search returned {} tags, request took {} ms", tags.size(), duration);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/ordered-by-usage")
    @Operation(summary = "Get tags ordered by usage", description = "Get tags ordered by number of associated todos")
    public ResponseEntity<List<Tag>> getTagsOrderedByUsage() {
        log.info("REST request to GET tags ordered by usage count");
        long startTime = System.currentTimeMillis();
        List<Tag> tags = tagService.getTagsOrderedByUsage();
        long duration = System.currentTimeMillis() - startTime;
        log.info("REST response: returning {} tags ordered by usage, request took {} ms", tags.size(), duration);
        return ResponseEntity.ok(tags);
    }
}
