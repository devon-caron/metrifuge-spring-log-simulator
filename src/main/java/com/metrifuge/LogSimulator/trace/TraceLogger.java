package com.metrifuge.LogSimulator.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TraceLogger {
    private static final Logger logger = LoggerFactory.getLogger("TRACE_LOG");

    public static void logSpanStart(String spanName, String parentSpanId) {
        String traceId = TraceContext.getTraceId();
        String spanId = TraceContext.generateSpanId();
        TraceContext.setSpanId(spanId);

        Map<String, Object> logData = new HashMap<>();
        logData.put("trace.trace_id", traceId);
        logData.put("trace.span_id", spanId);
        if (parentSpanId != null) {
            logData.put("trace.parent_id", parentSpanId);
        }
        logData.put("name", spanName);
        logData.put("type", "span_start");
        logData.put("timestamp", System.currentTimeMillis());

        logger.info("SPAN_START: {}", logData);
    }

    public static void logSpanEnd(String spanName, long durationMs) {
        String traceId = TraceContext.getTraceId();
        String spanId = TraceContext.getSpanId();

        Map<String, Object> logData = new HashMap<>();
        logData.put("trace.trace_id", traceId);
        logData.put("trace.span_id", spanId);
        logData.put("name", spanName);
        logData.put("type", "span_end");
        logData.put("duration_ms", durationMs);
        logData.put("timestamp", System.currentTimeMillis());

        logger.info("SPAN_END: {}", logData);
    }

    public static void logHttpRequest(String method, String url, int statusCode, long durationMs) {
        String traceId = TraceContext.getTraceId();
        String spanId = TraceContext.getSpanId();

        Map<String, Object> logData = new HashMap<>();
        logData.put("trace.trace_id", traceId);
        logData.put("trace.span_id", spanId);
        logData.put("type", "http_request");
        logData.put("http.method", method);
        logData.put("http.url", url);
        logData.put("http.status_code", statusCode);
        logData.put("duration_ms", durationMs);
        logData.put("timestamp", System.currentTimeMillis());

        logger.info("HTTP_REQUEST: {}", logData);
    }

    public static void logMetric(String metricName, double value, Map<String, String> tags) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("type", "metric");
        logData.put("metric.name", metricName);
        logData.put("metric.value", value);
        if (tags != null) {
            logData.put("metric.tags", tags);
        }
        logData.put("timestamp", System.currentTimeMillis());

        logger.info("METRIC: {}", logData);
    }

    public static void logError(String errorMessage, String errorType) {
        String traceId = TraceContext.getTraceId();
        String spanId = TraceContext.getSpanId();

        Map<String, Object> logData = new HashMap<>();
        logData.put("trace.trace_id", traceId);
        logData.put("trace.span_id", spanId);
        logData.put("type", "error");
        logData.put("error.message", errorMessage);
        logData.put("error.type", errorType);
        logData.put("timestamp", System.currentTimeMillis());

        logger.error("ERROR: {}", logData);
    }
}
