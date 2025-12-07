package com.metrifuge.LogSimulator.service;

import com.metrifuge.LogSimulator.model.Tag;
import com.metrifuge.LogSimulator.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        log.info("Fetching all tags from database");
        long startTime = System.currentTimeMillis();
        List<Tag> tags = tagRepository.findAll();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} tags in {} ms", tags.size(), duration);
        log.debug("Tags: {}", tags);
        return tags;
    }

    @Transactional(readOnly = true)
    public Tag getTagById(Long id) {
        log.info("Fetching tag with id: {}", id);
        long startTime = System.currentTimeMillis();
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> {
                log.error("Tag not found with id: {}", id);
                return new RuntimeException("Tag not found with id: " + id);
            });
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved tag '{}' in {} ms", tag.getName(), duration);
        log.debug("Tag details: {}", tag);
        return tag;
    }

    @Transactional
    public Tag createTag(Tag tag) {
        log.info("Creating new tag with name: '{}'", tag.getName());
        log.debug("Tag creation request: {}", tag);

        tagRepository.findByName(tag.getName()).ifPresent(existing -> {
            log.error("Tag with name '{}' already exists", tag.getName());
            throw new RuntimeException("Tag with name '" + tag.getName() + "' already exists");
        });

        long startTime = System.currentTimeMillis();
        Tag savedTag = tagRepository.save(tag);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully created tag with id: {} in {} ms", savedTag.getId(), duration);
        log.debug("Created tag details: {}", savedTag);
        return savedTag;
    }

    @Transactional
    public Tag updateTag(Long id, Tag tagDetails) {
        log.info("Updating tag with id: {}", id);
        log.debug("Update request: {}", tagDetails);

        Tag tag = getTagById(id);
        log.debug("Found existing tag: {}", tag);

        String oldName = tag.getName();

        if (!tag.getName().equals(tagDetails.getName())) {
            tagRepository.findByName(tagDetails.getName()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    log.error("Tag with name '{}' already exists", tagDetails.getName());
                    throw new RuntimeException("Tag with name '" + tagDetails.getName() + "' already exists");
                }
            });
        }

        tag.setName(tagDetails.getName());
        tag.setColor(tagDetails.getColor());

        long startTime = System.currentTimeMillis();
        Tag updatedTag = tagRepository.save(tag);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully updated tag {} in {} ms", id, duration);
        if (!oldName.equals(updatedTag.getName())) {
            log.info("Tag name changed from '{}' to '{}'", oldName, updatedTag.getName());
        }
        log.debug("Updated tag details: {}", updatedTag);

        return updatedTag;
    }

    @Transactional
    public void deleteTag(Long id) {
        log.info("Deleting tag with id: {}", id);
        Tag tag = getTagById(id);
        log.debug("Tag to delete: {}", tag);

        if (!tag.getTodos().isEmpty()) {
            log.warn("Deleting tag '{}' with {} associated todos", tag.getName(), tag.getTodos().size());
        }

        long startTime = System.currentTimeMillis();
        tagRepository.delete(tag);
        long duration = System.currentTimeMillis() - startTime;

        log.info("Successfully deleted tag {} in {} ms", id, duration);
        log.debug("Deleted tag had name: '{}'", tag.getName());
    }

    @Transactional(readOnly = true)
    public List<Tag> searchTags(String keyword) {
        log.info("Searching tags with keyword: '{}'", keyword);
        long startTime = System.currentTimeMillis();
        List<Tag> tags = tagRepository.searchByName(keyword);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Search returned {} tags in {} ms", tags.size(), duration);
        return tags;
    }

    @Transactional(readOnly = true)
    public List<Tag> getTagsOrderedByUsage() {
        log.info("Fetching tags ordered by usage");
        long startTime = System.currentTimeMillis();
        List<Tag> tags = tagRepository.findTagsOrderedByUsage();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Retrieved {} tags ordered by usage in {} ms", tags.size(), duration);
        return tags;
    }
}
