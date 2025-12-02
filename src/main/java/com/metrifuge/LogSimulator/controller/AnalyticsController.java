package com.metrifuge.LogSimulator.controller;

import com.metrifuge.LogSimulator.service.DataProcessingService;
import com.metrifuge.LogSimulator.trace.TraceContext;
import com.metrifuge.LogSimulator.trace.TraceLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final DataProcessingService dataProcessingService;

    public AnalyticsController(DataProcessingService dataProcessingService) {
        this.dataProcessingService = dataProcessingService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.getDashboard", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            List<String> sources = Arrays.asList("users", "posts", "comments", "metrics");
            List<Map<String, Object>> aggregatedData = dataProcessingService.aggregateData(sources);

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("data", aggregatedData);
            dashboard.put("generatedAt", System.currentTimeMillis());
            dashboard.put("sources", sources.size());

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.getDashboard", duration);

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.getDashboard", duration);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody Map<String, Object> reportConfig) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("controller.generateReport", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String reportType = (String) reportConfig.getOrDefault("reportType", "summary");
            Map<String, Object> processedReport = dataProcessingService.processData("reports", reportConfig);

            List<String> dataSources = Arrays.asList("analytics", "logs", "metrics");
            List<Map<String, Object>> aggregatedData = dataProcessingService.aggregateData(dataSources);

            Map<String, Object> report = new HashMap<>();
            report.put("reportType", reportType);
            report.put("metadata", processedReport);
            report.put("aggregatedData", aggregatedData);
            report.put("generatedAt", System.currentTimeMillis());

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("controller.generateReport", duration);

            return ResponseEntity.ok(report);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Controller error: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("controller.generateReport", duration);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(health);
    }
}
