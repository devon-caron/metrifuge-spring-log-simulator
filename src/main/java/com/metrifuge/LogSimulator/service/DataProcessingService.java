package com.metrifuge.LogSimulator.service;

import com.metrifuge.LogSimulator.trace.TraceContext;
import com.metrifuge.LogSimulator.trace.TraceLogger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataProcessingService {

    private final Random random = new Random();

    public Map<String, Object> processData(String dataType, Map<String, Object> inputData) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("business.processData", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            // Simulate database read
            simulateDatabaseRead(dataType);

            // Simulate some processing
            simulateProcessing();

            // Simulate database write
            simulateDatabaseWrite(dataType);

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("dataType", dataType);
            result.put("processedAt", System.currentTimeMillis());
            result.put("recordCount", random.nextInt(100) + 1);

            Map<String, String> tags = new HashMap<>();
            tags.put("dataType", dataType);
            TraceLogger.logMetric("business.data.processed", 1.0, tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("business.processData", duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Failed to process data: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("business.processData", duration);
            throw e;
        }
    }

    private void simulateDatabaseRead(String table) {
        String parentSpanId = TraceContext.getSpanId();
        String newSpanId = TraceContext.generateSpanId();
        String originalSpanId = TraceContext.getSpanId();
        TraceContext.setSpanId(newSpanId);

        TraceLogger.logSpanStart("db.read." + table, parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            // Simulate DB latency
            Thread.sleep(random.nextInt(50) + 10);

            Map<String, String> tags = new HashMap<>();
            tags.put("db.table", table);
            tags.put("db.operation", "SELECT");
            TraceLogger.logMetric("db.query.duration", random.nextInt(50) + 10, tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("db.read." + table, duration);
        } catch (InterruptedException e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Database read interrupted: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("db.read." + table, duration);
            Thread.currentThread().interrupt();
        } finally {
            TraceContext.setSpanId(originalSpanId);
        }
    }

    private void simulateDatabaseWrite(String table) {
        String parentSpanId = TraceContext.getSpanId();
        String newSpanId = TraceContext.generateSpanId();
        String originalSpanId = TraceContext.getSpanId();
        TraceContext.setSpanId(newSpanId);

        TraceLogger.logSpanStart("db.write." + table, parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            // Simulate DB latency
            Thread.sleep(random.nextInt(80) + 20);

            Map<String, String> tags = new HashMap<>();
            tags.put("db.table", table);
            tags.put("db.operation", "INSERT");
            TraceLogger.logMetric("db.query.duration", random.nextInt(80) + 20, tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("db.write." + table, duration);
        } catch (InterruptedException e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Database write interrupted: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("db.write." + table, duration);
            Thread.currentThread().interrupt();
        } finally {
            TraceContext.setSpanId(originalSpanId);
        }
    }

    private void simulateProcessing() {
        String parentSpanId = TraceContext.getSpanId();
        String newSpanId = TraceContext.generateSpanId();
        String originalSpanId = TraceContext.getSpanId();
        TraceContext.setSpanId(newSpanId);

        TraceLogger.logSpanStart("business.transform", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            // Simulate processing time
            Thread.sleep(random.nextInt(30) + 5);

            TraceLogger.logMetric("business.transform.operations", random.nextInt(10) + 1, null);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("business.transform", duration);
        } catch (InterruptedException e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Processing interrupted: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("business.transform", duration);
            Thread.currentThread().interrupt();
        } finally {
            TraceContext.setSpanId(originalSpanId);
        }
    }

    public List<Map<String, Object>> aggregateData(List<String> sources) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("business.aggregateData", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            List<Map<String, Object>> results = new ArrayList<>();

            for (String source : sources) {
                simulateDatabaseRead(source);
                Map<String, Object> data = new HashMap<>();
                data.put("source", source);
                data.put("count", random.nextInt(1000));
                results.add(data);
            }

            Map<String, String> tags = new HashMap<>();
            tags.put("sourceCount", String.valueOf(sources.size()));
            TraceLogger.logMetric("business.aggregate.sources", sources.size(), tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("business.aggregateData", duration);

            return results;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Failed to aggregate data: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("business.aggregateData", duration);
            throw e;
        }
    }
}
