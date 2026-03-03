package com.mitchmele.musicbrain_api.simulation

import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.*

import static io.gatling.javaapi.core.CoreDsl.*
import static io.gatling.javaapi.http.HttpDsl.*

class TopArtistsSimulation extends Simulation {

    String baseUrl = System.getProperty("baseUrl", "http://localhost:8080")

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(baseUrl)
            .acceptHeader("application/json")

    ScenarioBuilder topArtists = scenario("Top Artists")
            .repeat(2).on(
                    exec(http("GET /api/artists/top")
                            .get("/api/artists/top")
                            .check(status().is(200))
                    ).pause(1)
            )

    TopArtistsSimulation() {
        setUp(
                topArtists.injectOpen(rampUsers(50).during(10))
        ).protocols(httpProtocol)
                .assertions(
                        global().responseTime().max().lt(5000),
                        global().successfulRequests().percent().gt(95.0d)
                )
    }
}
