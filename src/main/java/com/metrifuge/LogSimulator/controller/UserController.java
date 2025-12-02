package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.model.Post;
import com.metrifuge.LogSimulator.model.User;
import com.metrifuge.LogSimulator.service.DataProcessingService;
import com.metrifuge.LogSimulator.service.ExternalApiService;
import com.metrifuge.LogSimulator.trace.TraceContext;
import com.metrifuge.LogSimulator.trace.TraceLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ExternalApiService externalApiService;
    private final DataProcessingService dataProcessingService;

    public UserController(ExternalApiService externalApiService, DataProcessingService dataProcessingService) {
        this.externalApiService = externalApiService;
        this.dataProcessingService = dataProcessingService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.getUser", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            User user = externalApiService.fetchUser(userId);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.getUser", duration);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.getUser", duration);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable Long userId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.getUserPosts", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            List<Post> posts = externalApiService.fetchUserPosts(userId);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.getUserPosts", duration);

            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.getUserPosts", duration);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.getUserProfile", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            // Fetch user from external API
            User user = externalApiService.fetchUser(userId);

            // Process user data
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("userId", userId);
            Map<String, Object> processedData = dataProcessingService.processData("user_profiles", inputData);

            // Fetch user posts
            List<Post> posts = externalApiService.fetchUserPosts(userId);

            Map<String, Object> profile = new HashMap<>();
            profile.put("user", user);
            profile.put("metadata", processedData);
            profile.put("postCount", posts.size());

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.getUserProfile", duration);

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.getUserProfile", duration);
            return ResponseEntity.internalServerError().build();
        }
    }
}
