package com.metrifuge.LogSimulator.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class TodoAppSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // Random data generators
  val random = new Random()

  val titles = List(
    "Complete project documentation",
    "Review pull requests",
    "Fix production bug",
    "Update dependencies",
    "Write unit tests",
    "Refactor legacy code",
    "Deploy to staging",
    "Database migration",
    "Security audit",
    "Performance optimization",
    "Code review",
    "Team meeting",
    "Client presentation",
    "Release preparation",
    "Hotfix deployment"
  )

  val descriptions = List(
    "This is a high priority task that needs immediate attention",
    "Long-term project task with multiple dependencies",
    "Critical bug affecting production users",
    "Routine maintenance task",
    "Enhancement request from stakeholder",
    "Technical debt reduction effort",
    "Infrastructure improvement",
    "Customer-facing feature development",
    "Internal tooling improvement",
    "Documentation update for new features"
  )

  val priorities = List("LOW", "MEDIUM", "HIGH", "URGENT")
  val statuses = List("TODO", "IN_PROGRESS", "BLOCKED", "REVIEW", "DONE")
  val assignees = List("alice", "bob", "charlie", "diana", "eve", "frank")

  val categoryNames = List("Work", "Personal", "Shopping", "Health", "Learning", "Projects")
  val tagNames = List("urgent", "bug", "feature", "documentation", "testing", "refactoring", "deployment", "meeting")

  // Feeders for random data
  val categoryFeeder = Iterator.continually(Map(
    "categoryName" -> categoryNames(random.nextInt(categoryNames.length)),
    "categoryDescription" -> s"Category for ${categoryNames(random.nextInt(categoryNames.length))} items",
    "categoryColor" -> f"#${random.nextInt(16777215)}%06x"
  ))

  val tagFeeder = Iterator.continually(Map(
    "tagName" -> tagNames(random.nextInt(tagNames.length)),
    "tagColor" -> f"#${random.nextInt(16777215)}%06x"
  ))

  val todoFeeder = Iterator.continually(Map(
    "title" -> titles(random.nextInt(titles.length)),
    "description" -> descriptions(random.nextInt(descriptions.length)),
    "priority" -> priorities(random.nextInt(priorities.length)),
    "status" -> statuses(random.nextInt(statuses.length)),
    "assignedTo" -> assignees(random.nextInt(assignees.length)),
    "estimatedHours" -> (random.nextInt(40) + 1),
    "completed" -> random.nextBoolean()
  ))

  // Scenario: Category CRUD Operations
  val categoryScenario = scenario("Category Management")
    .feed(categoryFeeder)
    .exec(http("Create Category")
      .post("/api/categories")
      .body(StringBody("""{"name":"${categoryName}","description":"${categoryDescription}","color":"${categoryColor}"}""")).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("categoryId")))
    .pause(1)
    .exec(http("Get All Categories")
      .get("/api/categories")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Category by ID")
      .get("/api/categories/${categoryId}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Search Categories")
      .get("/api/categories/search")
      .queryParam("keyword", "${categoryName}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Categories Ordered by Usage")
      .get("/api/categories/ordered-by-usage")
      .check(status.is(200)))

  // Scenario: Tag CRUD Operations
  val tagScenario = scenario("Tag Management")
    .feed(tagFeeder)
    .exec(http("Create Tag")
      .post("/api/tags")
      .body(StringBody("""{"name":"${tagName}","color":"${tagColor}"}""")).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("tagId")))
    .pause(1)
    .exec(http("Get All Tags")
      .get("/api/tags")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Tag by ID")
      .get("/api/tags/${tagId}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Search Tags")
      .get("/api/tags/search")
      .queryParam("keyword", "${tagName}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Tags Ordered by Usage")
      .get("/api/tags/ordered-by-usage")
      .check(status.is(200)))

  // Scenario: Todo CRUD Operations
  val todoScenario = scenario("Todo Management")
    .feed(todoFeeder)
    .exec(http("Create Todo")
      .post("/api/todos")
      .body(StringBody(
        """{"title":"${title}","description":"${description}","priority":"${priority}","status":"${status}","assignedTo":"${assignedTo}","estimatedHours":${estimatedHours},"completed":${completed}}"""
      )).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("todoId")))
    .pause(1)
    .exec(http("Get All Todos")
      .get("/api/todos")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Todo by ID")
      .get("/api/todos/${todoId}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Update Todo")
      .put("/api/todos/${todoId}")
      .body(StringBody(
        """{"title":"${title} - Updated","description":"${description}","priority":"${priority}","status":"${status}","assignedTo":"${assignedTo}","estimatedHours":${estimatedHours},"completed":${completed}}"""
      )).asJson
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Todos by Priority")
      .get("/api/todos/priority/${priority}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Todos by Status")
      .get("/api/todos/status/${status}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Todos by Assignee")
      .get("/api/todos/assigned/${assignedTo}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Search Todos")
      .get("/api/todos/search")
      .queryParam("keyword", "project")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Completed Todos")
      .get("/api/todos/completed/true")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Incomplete Todos")
      .get("/api/todos/completed/false")
      .check(status.is(200)))

  // Scenario: Complex operations with relationships
  val complexScenario = scenario("Complex Operations")
    .exec(http("Create Category for Complex Test")
      .post("/api/categories")
      .body(StringBody("""{"name":"Complex Test Category","description":"Category for complex testing","color":"#FF5733"}""")).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("complexCategoryId")))
    .pause(1)
    .exec(http("Create Tag 1 for Complex Test")
      .post("/api/tags")
      .body(StringBody("""{"name":"complex-tag-1","color":"#C70039"}""")).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("complexTagId1")))
    .pause(1)
    .exec(http("Create Tag 2 for Complex Test")
      .post("/api/tags")
      .body(StringBody("""{"name":"complex-tag-2","color":"#900C3F"}""")).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("complexTagId2")))
    .pause(1)
    .exec(http("Create Todo with Category and Tags")
      .post("/api/todos")
      .body(StringBody(
        """{"title":"Complex Todo with Relationships","description":"This todo has category and tags","priority":"HIGH","status":"IN_PROGRESS","category":{"id":${complexCategoryId}},"tags":[{"id":${complexTagId1}},{"id":${complexTagId2}}],"assignedTo":"alice","estimatedHours":8,"completed":false}"""
      )).asJson
      .check(status.is(201))
      .check(jsonPath("$.id").saveAs("complexTodoId")))
    .pause(1)
    .exec(http("Get Todos by Category")
      .get("/api/todos/category/${complexCategoryId}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Todos by Tag")
      .get("/api/todos/tag/${complexTagId1}")
      .check(status.is(200)))

  // Load simulation setup
  setUp(
    categoryScenario.inject(
      rampUsers(10) during (30 seconds)
    ),
    tagScenario.inject(
      rampUsers(10) during (30 seconds)
    ),
    todoScenario.inject(
      rampUsers(20) during (60 seconds)
    ),
    complexScenario.inject(
      rampUsers(5) during (30 seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(5000),
      global.successfulRequests.percent.gt(95)
    )
}
