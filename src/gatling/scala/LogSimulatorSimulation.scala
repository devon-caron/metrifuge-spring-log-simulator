import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LogSimulatorSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  val getUserScenario = scenario("Get User")
    .exec(http("Get User 1")
      .get("/api/users/1")
      .check(status.is(200)))
    .pause(1)

  val getUserPostsScenario = scenario("Get User Posts")
    .exec(http("Get User 2 Posts")
      .get("/api/users/2/posts")
      .check(status.is(200)))
    .pause(1)

  val getUserProfileScenario = scenario("Get User Profile")
    .exec(http("Get User 3 Profile")
      .get("/api/users/3/profile")
      .check(status.is(200)))
    .pause(2)

  val getPostScenario = scenario("Get Post")
    .exec(http("Get Post 1")
      .get("/api/content/posts/1")
      .check(status.is(200)))
    .pause(1)

  val getQuoteScenario = scenario("Get Quote")
    .exec(http("Get Random Quote")
      .get("/api/content/quote")
      .check(status.is(200)))
    .pause(1)

  val processContentScenario = scenario("Process Content")
    .exec(http("Process Content")
      .post("/api/content/process")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"type": "article", "content": "test content"}"""))
      .check(status.is(200)))
    .pause(1)

  val getDashboardScenario = scenario("Get Dashboard")
    .exec(http("Get Analytics Dashboard")
      .get("/api/analytics/dashboard")
      .check(status.is(200)))
    .pause(2)

  val generateReportScenario = scenario("Generate Report")
    .exec(http("Generate Analytics Report")
      .post("/api/analytics/report")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"reportType": "monthly", "includeDetails": true}"""))
      .check(status.is(200)))
    .pause(2)

  val healthCheckScenario = scenario("Health Check")
    .exec(http("Health Check")
      .get("/api/analytics/health")
      .check(status.is(200)))
    .pause(1)

  val mixedWorkloadScenario = scenario("Mixed Workload")
    .exec(http("Get User")
      .get("/api/users/${userId}")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Posts")
      .get("/api/users/${userId}/posts")
      .check(status.is(200)))
    .pause(1)
    .exec(http("Get Dashboard")
      .get("/api/analytics/dashboard")
      .check(status.is(200)))
    .pause(1)

  setUp(
    getUserScenario.inject(
      rampUsers(5).during(10.seconds),
      constantUsersPerSec(2).during(30.seconds)
    ),
    getUserPostsScenario.inject(
      rampUsers(5).during(10.seconds),
      constantUsersPerSec(2).during(30.seconds)
    ),
    getUserProfileScenario.inject(
      rampUsers(3).during(10.seconds),
      constantUsersPerSec(1).during(30.seconds)
    ),
    getPostScenario.inject(
      rampUsers(3).during(10.seconds),
      constantUsersPerSec(1).during(30.seconds)
    ),
    getQuoteScenario.inject(
      rampUsers(2).during(10.seconds),
      constantUsersPerSec(1).during(30.seconds)
    ),
    processContentScenario.inject(
      rampUsers(3).during(10.seconds),
      constantUsersPerSec(1).during(30.seconds)
    ),
    getDashboardScenario.inject(
      rampUsers(2).during(10.seconds),
      constantUsersPerSec(1).during(30.seconds)
    ),
    generateReportScenario.inject(
      rampUsers(2).during(10.seconds),
      constantUsersPerSec(1).during(30.seconds)
    ),
    healthCheckScenario.inject(
      constantUsersPerSec(3).during(40.seconds)
    ),
    mixedWorkloadScenario.inject(
      rampUsers(5).during(10.seconds),
      constantUsersPerSec(2).during(30.seconds)
    ).protocols(httpProtocol)
  ).protocols(httpProtocol)
}
