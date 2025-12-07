# Todo Application - Spring Boot Log Simulator

A comprehensive Todo list application built with Spring Boot, designed to generate extensive logs for monitoring and analysis. This application uses SQLite as the database and includes a Gatling test suite for load testing and log generation.

## Features

- **RESTful API** for Todo management (CRUD operations)
- **SQLite Database** for lightweight, file-based data persistence
- **Extensive Logging** at multiple levels (DEBUG, INFO, WARN, ERROR)
- **Request/Response Logging** with unique request IDs
- **Gatling Load Tests** for simulating realistic user traffic
- **Rich Domain Model** with priorities, categories, tags, and more
- **Statistics Endpoint** for aggregated data analysis
- **Input Validation** with detailed error messages
- **Exception Handling** with structured error responses
- **OpenAPI 3 / Swagger UI** for interactive API documentation
- **Docker Support** with multi-stage builds
- **Kubernetes Ready** with manifests and deployment guides

## Technology Stack

- Java 17
- Spring Boot 4.0.0
- Spring Data JPA
- SQLite Database
- Lombok
- Gatling (for load testing)
- Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### Build the Application

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Docker Deployment

### Build Docker Image

```bash
docker build -t todo-app:latest .
```

### Run with Docker

```bash
docker run -d \
  -p 8080:8080 \
  -v todo-data:/app/data \
  -v todo-logs:/app/logs \
  --name todo-app \
  todo-app:latest
```

### Run with Docker Compose

```bash
docker-compose up -d
```

Stop:
```bash
docker-compose down
```

View logs:
```bash
docker-compose logs -f
```

## Kubernetes Deployment

### Quick Deploy

```bash
# Build and load image (for minikube)
docker build -t todo-app:latest .
minikube image load todo-app:latest

# Deploy to cluster
kubectl apply -f k8s/

# Check status
kubectl get pods -l app=todo-app
```

### Access the Application

**Port Forward:**
```bash
kubectl port-forward svc/todo-app-service 8080:80
```

**NodePort (for minikube):**
```bash
minikube service todo-app-nodeport --url
```

**Using the deployment script:**
```bash
./build-and-deploy.sh
```

See [k8s/README.md](k8s/README.md) for detailed Kubernetes deployment guide.

### View Logs

Logs are written to both console and file:
- Console: Real-time log output
- File: `logs/todo-app.log`
- Docker: `docker logs todo-app -f`
- Kubernetes: `kubectl logs -l app=todo-app -f`

### API Documentation (Swagger UI)

Once the application is running, you can access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

The Swagger UI provides:
- Complete API documentation with request/response schemas
- Interactive testing of all endpoints
- Example values for all fields
- Validation rules and constraints
- Try-it-out functionality to test the API directly from the browser

## API Endpoints

### Create Todo
```bash
POST /api/todos
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the project",
  "priority": "HIGH",
  "category": "WORK",
  "assignedTo": "alice@example.com",
  "tags": "documentation",
  "estimatedHours": 8
}
```

### Get All Todos
```bash
GET /api/todos
```

### Get Todo by ID
```bash
GET /api/todos/{id}
```

### Filter Todos
```bash
# By completion status
GET /api/todos?completed=true

# By priority
GET /api/todos?priority=HIGH

# By category
GET /api/todos?category=WORK

# Search by keyword
GET /api/todos?search=bug
```

### Update Todo
```bash
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "URGENT",
  "category": "WORK",
  "completed": false
}
```

### Complete Todo
```bash
PATCH /api/todos/{id}/complete
```

### Delete Todo
```bash
DELETE /api/todos/{id}
```

### Get Statistics
```bash
GET /api/todos/statistics
```

### Health Check
```bash
GET /api/todos/health
```

## Domain Model

### Todo Entity

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Auto-generated ID |
| title | String | Todo title (required, max 500 chars) |
| description | String | Detailed description (max 2000 chars) |
| completed | Boolean | Completion status (default: false) |
| priority | Enum | LOW, MEDIUM, HIGH, URGENT |
| category | Enum | GENERAL, WORK, PERSONAL, SHOPPING, HEALTH, EDUCATION, FINANCE, OTHER |
| createdAt | LocalDateTime | Timestamp of creation |
| updatedAt | LocalDateTime | Timestamp of last update |
| dueDate | LocalDateTime | Due date (optional) |
| completedAt | LocalDateTime | Timestamp when completed |
| assignedTo | String | Assignee email (max 100 chars) |
| tags | String | Comma-separated tags (max 50 chars) |
| estimatedHours | Integer | Estimated effort in hours |

## Logging Configuration

The application generates extensive logs at multiple levels:

- **INFO**: Request/Response logging, service operations, statistics
- **DEBUG**: Detailed operation information, query parameters, state changes
- **TRACE**: SQL parameter bindings, header information
- **ERROR**: Exception details, validation errors, not found errors

### Log Format
```
2025-12-07 12:34:56.789 [http-nio-8080-exec-1] INFO  c.m.LogSimulator.controller.TodoController - POST /api/todos - Creating new todo
```

### Key Log Features
- Unique Request IDs for request tracing
- Performance timing for all requests
- Slow request detection (> 1 second)
- Database query logging
- State change tracking (completion, priority changes)

## Running Gatling Tests

### Start the Application
First, ensure the application is running:
```bash
mvn spring-boot:run
```

### Run Gatling Tests (in a new terminal)
```bash
mvn gatling:test
```

### Gatling Test Scenarios

The simulation includes 8 comprehensive scenarios:

1. **Create Todos**: Ramps up 50 users creating todos with randomized data
2. **Read Todos**: 30 users reading and filtering todos
3. **Update Todos**: 20 users updating existing todos
4. **Complete Todos**: 15 users marking todos as complete
5. **Search Todos**: 10 users searching with various keywords
6. **Get Statistics**: Continuous statistics monitoring
7. **Delete Todos**: 10 users creating and deleting todos
8. **Mixed Operations**: 25 users performing realistic mixed operations

### Load Profile
- **Duration**: 40 seconds
- **Total Virtual Users**: ~160 concurrent users at peak
- **Request Rate**: ~50-60 requests/second at peak
- **Randomized Data**: 30 titles, 20 descriptions, 4 priorities, 8 categories, 5 assignees, 8 tag types

### View Gatling Reports
After running tests, reports are available at:
```
target/gatling/todoapplicationsimulation-{timestamp}/index.html
```

## Sample Data

The application initializes with 15 sample todos on first run, including:
- Development tasks (bug fixes, code reviews, deployments)
- Personal tasks (shopping, health appointments)
- Educational tasks (online courses)
- Financial tasks (bill payments)

## Database

- **Type**: SQLite
- **File**: `todos.db` (created in application root directory)
- **Schema**: Auto-generated by Hibernate
- **Mode**: Update (preserves data between restarts)

### View Database
You can use any SQLite browser to view the database:
```bash
sqlite3 todos.db
.tables
SELECT * FROM todos;
```

## Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/metrifuge/LogSimulator/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── dto/             # Data transfer objects
│   │       ├── exception/       # Exception handling
│   │       ├── model/           # Domain entities
│   │       ├── repository/      # Data repositories
│   │       └── service/         # Business logic
│   └── resources/
│       └── application.yaml     # Application configuration
└── test/
    └── scala/
        └── simulations/         # Gatling test scenarios
```

### Adding Custom Logs

To add more logging throughout the application:

1. Inject `@Slf4j` annotation (using Lombok)
2. Use appropriate log levels:
   - `log.debug()` - Detailed debugging information
   - `log.info()` - Important business events
   - `log.warn()` - Warning conditions
   - `log.error()` - Error conditions

Example:
```java
@Slf4j
@Service
public class MyService {
    public void myMethod() {
        log.info("Starting important operation");
        log.debug("Processing with parameters: {}", params);
    }
}
```

## Monitoring & Analysis

### Log Analysis
The logs generated by this application include:

- HTTP request/response details
- Database queries and parameters
- Business operation details
- Performance metrics
- Error traces

### Use Cases for Logs
- Performance monitoring
- Debugging issues
- Audit trail
- Usage analytics
- Load testing verification
- Database query optimization

## Customization

### Adjust Logging Levels
Edit `src/main/resources/application.yaml`:

```yaml
logging:
  level:
    root: INFO
    com.metrifuge.LogSimulator: DEBUG
```

### Modify Load Test Parameters
Edit `src/test/scala/simulations/TodoApplicationSimulation.scala` to adjust:
- Number of users
- Ramp-up time
- Request rate
- Test duration

## Troubleshooting

### Port Already in Use
Change the port in `application.yaml`:
```yaml
server:
  port: 8081
```

### Database Locked
Stop all running instances and delete `todos.db` file, then restart.

### Gatling Tests Failing
Ensure the Spring Boot application is running before executing Gatling tests.

## License

This is a demonstration project for log simulation and testing purposes.

## Contributors

- Metrifuge Team
