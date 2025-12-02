package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.model.Post;
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
@RequestMapping("/api/content")
public class ContentController {

    private final ExternalApiService externalApiService;
    private final DataProcessingService dataProcessingService;

    public ContentController(ExternalApiService externalApiService, DataProcessingService dataProcessingService) {
        this.externalApiService = externalApiService;
        this.dataProcessingService = dataProcessingService;
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.getPost", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            Post post = externalApiService.fetchPost(postId);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.getPost", duration);

            return ResponseEntity.ok(post);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.getPost", duration);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/quote")
    public ResponseEntity<Map<String, Object>> getQuote() {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.getQuote", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String quote = externalApiService.fetchRandomQuote();

            Map<String, Object> response = new HashMap<>();
            response.put("quote", quote);
            response.put("fetchedAt", System.currentTimeMillis());

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.getQuote", duration);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.getQuote", duration);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processContent(@RequestBody Map<String, Object> data) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.processContent", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String dataType = (String) data.getOrDefault("type", "content");
            Map<String, Object> result = dataProcessingService.processData(dataType, data);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.processContent", duration);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.processContent", duration);
            return ResponseEntity.internalServerError().build();
        }
    }
}
