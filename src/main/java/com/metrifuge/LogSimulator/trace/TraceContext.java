package com.metrifuge.LogSimulator.trace;

import java.util.Random;

public class TraceContext {
    private static final ThreadLocal<String> traceId = new ThreadLocal<>();
    private static final ThreadLocal<String> spanId = new ThreadLocal<>();
    private static final Random random = new Random();

    public static String getTraceId() {
        return traceId.get();
    }

    public static void setTraceId(String id) {
        traceId.set(id);
    }

    public static String getSpanId() {
        return spanId.get();
    }

    public static void setSpanId(String id) {
        spanId.set(id);
    }

    public static String generateTraceId() {
        return generateId(16);
    }

    public static String generateSpanId() {
        return generateId(8);
    }

    private static String generateId(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString();
    }

    public static void clear() {
        traceId.remove();
        spanId.remove();
    }
}
