package com.mitchmele.musicbrain_api.simulation

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*

import static io.gatling.javaapi.core.CoreDsl.*
import static io.gatling.javaapi.http.HttpDsl.*

class FullLoadSimulation extends Simulation {

    String baseUrl = System.getProperty("baseUrl", "http://localhost:8080")

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(baseUrl)
            .acceptHeader("application/json")

    ScenarioBuilder fullLoad = scenario("Full Load - All Endpoints")
            .exec(http("GET /api/artists/top")
                    .get("/api/artists/top")
                    .check(status().is(200))
            ).pause(1)
            .exec(http("GET /api/tracks/recent")
                    .get("/api/tracks/recent")
                    .check(status().is(200))
            ).pause(1)
            .exec(http("GET /api/genres/top")
                    .get("/api/genres/top")
                    .check(status().is(200))
            ).pause(1)
            .exec(http("GET /api/discovery/suggested")
                    .get("/api/discovery/suggested")
                    .check(status().is(200))
            ).pause(1)

    FullLoadSimulation() {
        setUp(
                fullLoad.injectOpen(rampUsers(25).during(10))
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(5000),
                        global().successfulRequests().percent().gt(95.0d)
                )
    }
}
