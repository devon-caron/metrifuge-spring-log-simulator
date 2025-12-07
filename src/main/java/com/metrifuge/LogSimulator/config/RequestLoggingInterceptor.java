package com.metrifuge.LogSimulator.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_ATTR = "requestId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        request.setAttribute(REQUEST_ID_ATTR, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        log.info("==> Incoming Request [{}] - {} {} from {}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        log.debug("Request [{}] - Query String: {}", requestId, request.getQueryString());
        log.debug("Request [{}] - Content Type: {}", requestId, request.getContentType());
        log.debug("Request [{}] - User Agent: {}", requestId, request.getHeader("User-Agent"));

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.trace("Request [{}] - Header: {} = {}", requestId, headerName, request.getHeader(headerName));
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTR);
        Long startTime = (Long) request.getAttribute("startTime");

        long duration = System.currentTimeMillis() - startTime;

        if (ex != null) {
            log.error("<== Request [{}] - Completed with ERROR in {}ms - Status: {} - Exception: {}",
                    requestId,
                    duration,
                    response.getStatus(),
                    ex.getMessage());
        } else {
            log.info("<== Request [{}] - Completed in {}ms - Status: {}",
                    requestId,
                    duration,
                    response.getStatus());
        }

        log.debug("Request [{}] - Response Content Type: {}", requestId, response.getContentType());

        if (duration > 1000) {
            log.warn("SLOW REQUEST DETECTED [{}] - Took {}ms", requestId, duration);
        }
    }
}
