# Todo Application - Spring Boot Log Simulator

A comprehensive Todo List application built with Spring Boot that generates extensive logs for testing and monitoring purposes. This application features a SQLite database, complete CRUD operations, advanced filtering, and Gatling performance tests.

## Features

### Application Features
- **Complex Todo Management**: Create, read, update, and delete todos with multiple attributes
- **Categories**: Organize todos into categories
- **Tags**: Flexible tagging system with many-to-many relationships
- **Advanced Filtering**: Filter by priority, status, assignee, due date, and more
- **Search Functionality**: Full-text search across todos, categories, and tags
- **SQLite Database**: Lightweight, file-based database for easy deployment
- **Extensive Logging**: Comprehensive logging at all layers (Controller, Service, Repository)

### Todo Attributes
- Title and description
- Priority levels: LOW, MEDIUM, HIGH, URGENT
- Status workflow: TODO, IN_PROGRESS, BLOCKED, REVIEW, DONE
- Due dates and completion tracking
- Time estimation (estimated/actual hours)
- Assignment to users
- Category association
- Multiple tags

## Technology Stack

- **Spring Boot 4.0.0**
- **Java 25**
- **SQLite** for database
- **Spring Data JPA** for data access
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI 3** for API documentation
- **Gatling 3.13.1** for load testing
- **Logback** for logging

## Project Structure

```
src/
├── main/
│   ├── java/com/metrifuge/LogSimulator/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── exception/       # Global exception handling
│   │   ├── model/          # Entity models (Todo, Category, Tag)
│   │   ├── repository/     # JPA repositories
│   │   └── service/        # Business logic layer
│   └── resources/
│       ├── application.properties
│       └── logback-spring.xml
└── test/
    └── java/com/metrifuge/LogSimulator/
        └── gatling/        # Gatling simulation tests
```

## API Endpoints

### Todos
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get todo by ID
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo
- `GET /api/todos/completed/{completed}` - Filter by completion status
- `GET /api/todos/priority/{priority}` - Filter by priority
- `GET /api/todos/status/{status}` - Filter by status
- `GET /api/todos/category/{categoryId}` - Filter by category
- `GET /api/todos/tag/{tagId}` - Filter by tag
- `GET /api/todos/search?keyword={keyword}` - Search todos
- `GET /api/todos/assigned/{assignedTo}` - Filter by assignee
- `GET /api/todos/due?start={start}&end={end}` - Filter by due date range

### Categories
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create new category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category
- `GET /api/categories/search?keyword={keyword}` - Search categories
- `GET /api/categories/ordered-by-usage` - Get categories by usage

### Tags
- `GET /api/tags` - Get all tags
- `GET /api/tags/{id}` - Get tag by ID
- `POST /api/tags` - Create new tag
- `PUT /api/tags/{id}` - Update tag
- `DELETE /api/tags/{id}` - Delete tag
- `GET /api/tags/search?keyword={keyword}` - Search tags
- `GET /api/tags/ordered-by-usage` - Get tags by usage

## Getting Started

### Prerequisites
- Java 25 or higher
- Gradle 8.x

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd metrifuge-spring-log-simulator
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

   The application will start on `http://localhost:8080`

4. **Access API Documentation**
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI JSON: `http://localhost:8080/api-docs`
   - Static OpenAPI spec: See `openapi.json` in project root

### Running Gatling Tests

The Gatling test suite simulates realistic load on the application and generates test data:

```bash
./gradlew gatlingRun
```

The Gatling tests will:
- Create categories, tags, and todos with randomized data
- Test all CRUD operations
- Test filtering and search endpoints
- Test complex operations with relationships
- Generate performance reports in `build/gatling-results/`

### Viewing Logs

Logs are generated in multiple files:
- `logs/todo-app.log` - Main application logs
- `logs/sql.log` - SQL queries and parameters
- `logs/http.log` - HTTP request/response logs

Console output also displays all logs with color coding.

## Example API Usage

### Create a Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Work",
    "description": "Work-related tasks",
    "color": "#FF5733"
  }'
```

### Create a Tag
```bash
curl -X POST http://localhost:8080/api/tags \
  -H "Content-Type: application/json" \
  -d '{
    "name": "urgent",
    "color": "#C70039"
  }'
```

### Create a Todo with Category and Tags
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete project documentation",
    "description": "Write comprehensive documentation for the new feature",
    "priority": "HIGH",
    "status": "IN_PROGRESS",
    "category": {"id": 1},
    "tags": [{"id": 1}, {"id": 2}],
    "assignedTo": "alice",
    "estimatedHours": 8,
    "completed": false
  }'
```

### Search Todos
```bash
curl http://localhost:8080/api/todos/search?keyword=documentation
```

### Filter by Priority
```bash
curl http://localhost:8080/api/todos/priority/HIGH
```

## Database

The application uses SQLite with the database file created at `todo_app.db` in the project root. The schema is automatically created and updated by Hibernate.

### Entity Relationships
- **Todo** ← Many-to-One → **Category**
- **Todo** ← Many-to-Many → **Tag**

## Logging Configuration

The application is configured with extensive logging:

- **DEBUG level** for application code (`com.metrifuge`)
- **DEBUG level** for Spring Web
- **DEBUG level** for Hibernate SQL
- **TRACE level** for SQL parameters

Each operation logs:
- Entry and exit points
- Execution duration
- Request/response details
- Validation results
- Error details with stack traces

## Performance Testing

The Gatling simulation includes:
- **Category scenario**: 10 concurrent users over 30 seconds
- **Tag scenario**: 10 concurrent users over 30 seconds
- **Todo scenario**: 20 concurrent users over 60 seconds
- **Complex scenario**: 5 concurrent users over 30 seconds

Performance assertions:
- Maximum response time < 5000ms
- Success rate > 95%

## OpenAPI Documentation

A complete OpenAPI v3 specification is available in:
1. `openapi.json` - Static specification file
2. `http://localhost:8080/api-docs` - Live API documentation
3. `http://localhost:8080/swagger-ui.html` - Interactive API explorer

## License

MIT License
