package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class TodoApplicationSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling-LoadTest/1.0")

  // Random data generators
  val random = new Random()

  val titles = List(
    "Complete project documentation",
    "Review pull requests",
    "Fix critical bug in production",
    "Implement new feature",
    "Refactor legacy code",
    "Update dependencies",
    "Write unit tests",
    "Setup CI/CD pipeline",
    "Database optimization",
    "Security audit",
    "Performance testing",
    "Code review",
    "Team meeting",
    "Client presentation",
    "Budget planning",
    "Hire new developer",
    "Update API documentation",
    "Migrate to cloud",
    "Implement caching",
    "Fix memory leak",
    "Setup monitoring",
    "Backup database",
    "Deploy to staging",
    "Release version 2.0",
    "Research new technologies",
    "Attend conference",
    "Training session",
    "Sprint planning",
    "Retrospective meeting",
    "Update README"
  )

  val descriptions = List(
    "This is an important task that needs immediate attention",
    "Low priority task that can be done later",
    "Critical issue affecting users",
    "Enhancement requested by product team",
    "Technical debt that needs to be addressed",
    "Routine maintenance task",
    "New feature for Q4 roadmap",
    "Bug reported by customer",
    "Internal improvement",
    "Compliance requirement",
    "Performance optimization needed",
    "User experience improvement",
    "Backend refactoring",
    "Frontend redesign",
    "API endpoint addition",
    "Database schema update",
    "Security vulnerability fix",
    "Integration with third-party service",
    "Monitoring and alerting setup",
    "Documentation update required"
  )

  val priorities = List("LOW", "MEDIUM", "HIGH", "URGENT")
  val categories = List("GENERAL", "WORK", "PERSONAL", "SHOPPING", "HEALTH", "EDUCATION", "FINANCE", "OTHER")
  val assignees = List("alice@example.com", "bob@example.com", "charlie@example.com", "diana@example.com", "eve@example.com")
  val tags = List("backend", "frontend", "devops", "testing", "documentation", "bug", "feature", "enhancement")

  // Feeder for creating random todos
  val todoFeeder = Iterator.continually(Map(
    "title" -> titles(random.nextInt(titles.length)),
    "description" -> descriptions(random.nextInt(descriptions.length)),
    "priority" -> priorities(random.nextInt(priorities.length)),
    "category" -> categories(random.nextInt(categories.length)),
    "assignedTo" -> assignees(random.nextInt(assignees.length)),
    "tags" -> tags(random.nextInt(tags.length)),
    "estimatedHours" -> (random.nextInt(20) + 1),
    "completed" -> random.nextBoolean()
  ))

  // Scenarios

  // Scenario 1: Create many todos
  val createTodos = scenario("Create Todos")
    .feed(todoFeeder)
    .exec(
      http("Create Todo")
        .post("/api/todos")
        .body(StringBody(
          """{
            |  "title": "${title}",
            |  "description": "${description}",
            |  "priority": "${priority}",
            |  "category": "${category}",
            |  "assignedTo": "${assignedTo}",
            |  "tags": "${tags}",
            |  "estimatedHours": ${estimatedHours},
            |  "completed": ${completed}
            |}""".stripMargin
        ))
        .check(status.is(201))
        .check(jsonPath("$.id").saveAs("todoId"))
    )
    .pause(100.milliseconds, 500.milliseconds)

  // Scenario 2: Read todos
  val readTodos = scenario("Read Todos")
    .exec(
      http("Get All Todos")
        .get("/api/todos")
        .check(status.is(200))
    )
    .pause(200.milliseconds, 800.milliseconds)
    .exec(
      http("Get Completed Todos")
        .get("/api/todos?completed=true")
        .check(status.is(200))
    )
    .pause(200.milliseconds, 800.milliseconds)
    .exec(
      http("Get High Priority Todos")
        .get("/api/todos?priority=HIGH")
        .check(status.is(200))
    )
    .pause(200.milliseconds, 800.milliseconds)
    .exec(
      http("Get Work Category Todos")
        .get("/api/todos?category=WORK")
        .check(status.is(200))
    )
    .pause(200.milliseconds, 800.milliseconds)

  // Scenario 3: Update todos
  val updateTodos = scenario("Update Todos")
    .feed(todoFeeder)
    .exec(
      http("Create Todo for Update")
        .post("/api/todos")
        .body(StringBody(
          """{
            |  "title": "${title}",
            |  "description": "${description}",
            |  "priority": "${priority}",
            |  "category": "${category}",
            |  "completed": false
            |}""".stripMargin
        ))
        .check(status.is(201))
        .check(jsonPath("$.id").saveAs("todoId"))
    )
    .pause(500.milliseconds, 1.second)
    .exec(
      http("Update Todo")
        .put("/api/todos/${todoId}")
        .body(StringBody(
          """{
            |  "title": "${title} - Updated",
            |  "description": "${description} - Updated content",
            |  "priority": "HIGH",
            |  "category": "${category}",
            |  "completed": true
            |}""".stripMargin
        ))
        .check(status.is(200))
    )
    .pause(200.milliseconds, 500.milliseconds)

  // Scenario 4: Complete todos
  val completeTodos = scenario("Complete Todos")
    .feed(todoFeeder)
    .exec(
      http("Create Todo for Completion")
        .post("/api/todos")
        .body(StringBody(
          """{
            |  "title": "${title}",
            |  "description": "${description}",
            |  "priority": "${priority}",
            |  "category": "${category}",
            |  "completed": false
            |}""".stripMargin
        ))
        .check(status.is(201))
        .check(jsonPath("$.id").saveAs("todoId"))
    )
    .pause(500.milliseconds, 1.second)
    .exec(
      http("Complete Todo")
        .patch("/api/todos/${todoId}/complete")
        .check(status.is(200))
        .check(jsonPath("$.completed").is("true"))
    )
    .pause(200.milliseconds, 500.milliseconds)

  // Scenario 5: Search todos
  val searchTodos = scenario("Search Todos")
    .exec(
      http("Search for 'bug'")
        .get("/api/todos?search=bug")
        .check(status.is(200))
    )
    .pause(500.milliseconds, 1.second)
    .exec(
      http("Search for 'feature'")
        .get("/api/todos?search=feature")
        .check(status.is(200))
    )
    .pause(500.milliseconds, 1.second)
    .exec(
      http("Search for 'project'")
        .get("/api/todos?search=project")
        .check(status.is(200))
    )
    .pause(200.milliseconds, 500.milliseconds)

  // Scenario 6: Get statistics
  val getStatistics = scenario("Get Statistics")
    .exec(
      http("Get Statistics")
        .get("/api/todos/statistics")
        .check(status.is(200))
        .check(jsonPath("$.total").exists)
        .check(jsonPath("$.completed").exists)
        .check(jsonPath("$.incomplete").exists)
    )
    .pause(1.second, 2.seconds)

  // Scenario 7: Delete todos
  val deleteTodos = scenario("Delete Todos")
    .feed(todoFeeder)
    .exec(
      http("Create Todo for Deletion")
        .post("/api/todos")
        .body(StringBody(
          """{
            |  "title": "${title}",
            |  "description": "${description}",
            |  "priority": "${priority}",
            |  "category": "${category}"
            |}""".stripMargin
        ))
        .check(status.is(201))
        .check(jsonPath("$.id").saveAs("todoId"))
    )
    .pause(500.milliseconds, 1.second)
    .exec(
      http("Delete Todo")
        .delete("/api/todos/${todoId}")
        .check(status.is(204))
    )
    .pause(200.milliseconds, 500.milliseconds)

  // Scenario 8: Mixed operations (realistic user behavior)
  val mixedOperations = scenario("Mixed Operations")
    .feed(todoFeeder)
    .exec(
      http("Create Todo")
        .post("/api/todos")
        .body(StringBody(
          """{
            |  "title": "${title}",
            |  "description": "${description}",
            |  "priority": "${priority}",
            |  "category": "${category}",
            |  "assignedTo": "${assignedTo}",
            |  "tags": "${tags}",
            |  "estimatedHours": ${estimatedHours}
            |}""".stripMargin
        ))
        .check(status.is(201))
        .check(jsonPath("$.id").saveAs("todoId"))
    )
    .pause(300.milliseconds, 1.second)
    .exec(
      http("Get Todo by ID")
        .get("/api/todos/${todoId}")
        .check(status.is(200))
    )
    .pause(300.milliseconds, 1.second)
    .exec(
      http("Get All Todos")
        .get("/api/todos")
        .check(status.is(200))
    )
    .pause(300.milliseconds, 1.second)
    .exec(
      http("Update Todo")
        .put("/api/todos/${todoId}")
        .body(StringBody(
          """{
            |  "title": "${title} - Modified",
            |  "description": "${description}",
            |  "priority": "URGENT",
            |  "category": "${category}",
            |  "completed": false
            |}""".stripMargin
        ))
        .check(status.is(200))
    )
    .pause(300.milliseconds, 1.second)
    .exec(
      http("Get Statistics")
        .get("/api/todos/statistics")
        .check(status.is(200))
    )
    .pause(200.milliseconds, 500.milliseconds)

  // Load simulation setup
  setUp(
    createTodos.inject(
      rampUsers(50) during (10.seconds),
      constantUsersPerSec(10) during (30.seconds)
    ),
    readTodos.inject(
      rampUsers(30) during (5.seconds),
      constantUsersPerSec(15) during (35.seconds)
    ),
    updateTodos.inject(
      nothingFor(5.seconds),
      rampUsers(20) during (10.seconds),
      constantUsersPerSec(5) during (25.seconds)
    ),
    completeTodos.inject(
      nothingFor(8.seconds),
      rampUsers(15) during (10.seconds),
      constantUsersPerSec(3) during (22.seconds)
    ),
    searchTodos.inject(
      nothingFor(10.seconds),
      rampUsers(10) during (5.seconds),
      constantUsersPerSec(5) during (25.seconds)
    ),
    getStatistics.inject(
      constantUsersPerSec(2) during (40.seconds)
    ),
    deleteTodos.inject(
      nothingFor(15.seconds),
      rampUsers(10) during (10.seconds),
      constantUsersPerSec(2) during (15.seconds)
    ),
    mixedOperations.inject(
      nothingFor(3.seconds),
      rampUsers(25) during (10.seconds),
      constantUsersPerSec(8) during (27.seconds)
    )
  ).protocols(httpProtocol)
}
