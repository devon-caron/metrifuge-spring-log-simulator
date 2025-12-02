package com.metrifuge.LogSimulator.config;

import com.metrifuge.LogSimulator.trace.TraceContext;
import com.metrifuge.LogSimulator.trace.TraceLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = TraceContext.generateTraceId();
        }
        TraceContext.setTraceId(traceId);

        String spanId = TraceContext.generateSpanId();
        TraceContext.setSpanId(spanId);

        request.setAttribute("requestStartTime", System.currentTimeMillis());

        TraceLogger.logSpanStart("http.request " + request.getMethod() + " " + request.getRequestURI(), null);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("requestStartTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("http.request " + request.getMethod() + " " + request.getRequestURI(), duration);
            TraceLogger.logHttpRequest(request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        }

        TraceContext.clear();
    }
}
