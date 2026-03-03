# Gatling Performance Tests

Load tests for the MusicBrain API using [Gatling](https://gatling.io/) with Groovy simulations and the Gatling Java API.

## Prerequisites

- The MusicBrain API running locally (or a target URL)
- A valid `LASTFM_API_KEY` environment variable set for the running app

## Simulations

| Simulation | Endpoint | Users | Requests/User |
|---|---|---|---|
| `TopArtistsSimulation` | `GET /api/artists/top` | 50 | 2 |
| `RecentTracksSimulation` | `GET /api/tracks/recent` | 50 | 2 |
| `TopGenresSimulation` | `GET /api/genres/top` | 50 | 2 |
| `DiscoverySimulation` | `GET /api/discovery/suggested` | 50 | 2 |
| `FullLoadSimulation` | All 4 endpoints | 25 | 4 (one per endpoint) |

All simulations ramp users over 10 seconds with 1-second pauses between requests.

## Assertions

Each simulation asserts:
- Max response time < 5 seconds
- Success rate > 95%

## Running

Start the app in one terminal:

```bash
LASTFM_API_KEY=your_key ./mvnw spring-boot:run
```

Then in another terminal:

```bash
# Run all simulations
./mvnw gatling:test -Pgatling

# Run a single simulation
./mvnw gatling:test -Pgatling \
  -Dgatling.simulationClass=com.mitchmele.musicbrain_api.simulation.TopArtistsSimulation

# Target a different environment
./mvnw gatling:test -Pgatling -DbaseUrl=https://staging.example.com
```

## Reports

After a run, Gatling generates an HTML report in `target/gatling/`. Open the `index.html` file in a browser to review response time distributions, throughput, and error rates.
