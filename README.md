# Metrifuge Spring Log Simulator

A Spring Boot application designed to generate realistic trace-like logs without actual OpenTelemetry instrumentation. This application simulates a real-world service making external API calls and performing business logic, with comprehensive logging that mimics distributed tracing patterns.

## Features

- **Fake Trace Generation**: Generates trace IDs, span IDs, and parent-child span relationships
- **External API Calls**: Makes requests to public APIs (JSONPlaceholder, Quotable.io) for realistic scenarios
- **Comprehensive Logging**: Logs include:
  - Trace IDs and Span IDs
  - Operation names and durations
  - HTTP request details (method, URL, status code)
  - Metrics (custom counters and gauges)
  - Error tracking
- **Multiple Endpoints**: Various REST endpoints simulating different business operations
- **Gatling Load Testing**: Pre-configured Gatling scenarios to generate traffic and logs

## Architecture

The application includes:

### Controllers
- **UserController** (`/api/users`): User-related endpoints with external API calls
- **ContentController** (`/api/content`): Content management endpoints
- **AnalyticsController** (`/api/analytics`): Analytics and reporting endpoints

### Services
- **ExternalApiService**: Makes calls to external public APIs (JSONPlaceholder, Quotable)
- **DataProcessingService**: Simulates database operations and business logic

### Trace Components
- **TraceContext**: ThreadLocal storage for trace/span IDs
- **TraceLogger**: Structured logging that mimics OpenTelemetry spans
- **TraceInterceptor**: HTTP interceptor that initializes trace context per request

## Prerequisites

- Java 25 (or compatible version)
- Gradle

## Running the Application

### Start the Spring Boot application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Test the endpoints:
```bash
# Get a user
curl http://localhost:8080/api/users/1

# Get user posts
curl http://localhost:8080/api/users/1/posts

# Get user profile (combines multiple operations)
curl http://localhost:8080/api/users/1/profile

# Get a post
curl http://localhost:8080/api/content/posts/1

# Get a random quote
curl http://localhost:8080/api/content/quote

# Process content
curl -X POST http://localhost:8080/api/content/process \
  -H "Content-Type: application/json" \
  -d '{"type": "article", "content": "test"}'

# Get analytics dashboard
curl http://localhost:8080/api/analytics/dashboard

# Generate report
curl -X POST http://localhost:8080/api/analytics/report \
  -H "Content-Type: application/json" \
  -d '{"reportType": "monthly", "includeDetails": true}'

# Health check
curl http://localhost:8080/api/analytics/health
```

## Running Gatling Load Tests

### Run Gatling simulation:
```bash
./gradlew gatlingRun
```

This will:
1. Start generating traffic to all endpoints
2. Create realistic trace logs with multiple concurrent users
3. Generate a detailed HTML report of the load test results

The Gatling report will be available at:
```
build/reports/gatling/logsimulatorSimulation-<timestamp>/index.html
```

## Log Format

Logs are generated in a structured format that mimics distributed tracing:

### Span Start
```
SPAN_START: {trace.trace_id=abc123, trace.span_id=def456, trace.parent_id=xyz789, name=http.request GET /api/users/1, type=span_start, timestamp=1234567890}
```

### Span End
```
SPAN_END: {trace.trace_id=abc123, trace.span_id=def456, name=http.request GET /api/users/1, type=span_end, duration_ms=150, timestamp=1234567890}
```

### HTTP Request
```
HTTP_REQUEST: {trace.trace_id=abc123, trace.span_id=def456, type=http_request, http.method=GET, http.url=https://jsonplaceholder.typicode.com/users/1, http.status_code=200, duration_ms=120, timestamp=1234567890}
```

### Metrics
```
METRIC: {type=metric, metric.name=external.api.user.fetch, metric.value=1.0, metric.tags={userId=1}, timestamp=1234567890}
```

### Errors
```
ERROR: {trace.trace_id=abc123, trace.span_id=def456, type=error, error.message=Connection timeout, error.type=TimeoutException, timestamp=1234567890}
```

## Use Cases

This application is perfect for:

1. **Testing Observability Pipelines**: Generate realistic log data for testing log aggregation and analysis tools
2. **Demo Environments**: Showcase observability tools without actual instrumentation
3. **Load Testing**: Use Gatling to generate sustained traffic and observe system behavior
4. **Log Parser Development**: Develop and test log parsing tools with structured trace-like logs
5. **Visualization Testing**: Feed logs into tools like Honeycomb, Datadog, or custom dashboards

## Configuration

Edit `src/main/resources/application.properties` to customize:
- Server port
- Logging levels
- WebClient settings

## Notes

- The application does NOT use actual OpenTelemetry instrumentation
- All trace IDs and span IDs are randomly generated
- External API calls are real (to public APIs like JSONPlaceholder)
- Database operations are simulated with Thread.sleep() delays
- Metrics are logged, not sent to actual metric collectors
