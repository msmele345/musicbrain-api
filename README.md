# MusicBrain API

A Spring Boot REST API that surfaces personalized music data from [Last.fm](https://www.last.fm/) — top artists, recent tracks, genre tags, and artist discovery.

## Prerequisites

- Java 21+
- A [Last.fm API key](https://www.last.fm/api/account/create)

## Configuration

Set your Last.fm API key as an environment variable:

```bash
export LASTFM_API_KEY=your_key_here
```

The Last.fm username is configured in `src/main/resources/application.properties`.

## Running Locally

```bash
LASTFM_API_KEY=your_key ./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/api/artists/top` | Top artists by play count | `period` (default: `14day`), `limit` (default: `10`) |
| GET | `/api/tracks/recent` | Recently played tracks | `limit` (default: `20`) |
| GET | `/api/genres/top` | Top genre tags | — |
| GET | `/api/discovery/suggested` | Suggested artists based on your top artist | — |

### Examples

```bash
curl http://localhost:8080/api/artists/top
curl http://localhost:8080/api/artists/top?period=overall&limit=5
curl http://localhost:8080/api/tracks/recent?limit=10
curl http://localhost:8080/api/genres/top
curl http://localhost:8080/api/discovery/suggested
```

## Caching

Responses are cached with [Caffeine](https://github.com/ben-manes/caffeine) to reduce Last.fm API calls. Cache TTL is 30 minutes. Caching is configured in `CacheConfig.java`.

## Testing

### Unit Tests

```bash
./mvnw test
```

### Gatling Performance Tests

Load tests use [Gatling](https://gatling.io/) with Groovy simulations. Start the app first, then in a separate terminal:

```bash
# Run all simulations
./mvnw gatling:test -Pgatling

# Run a single simulation
./mvnw gatling:test -Pgatling \
  -Dgatling.simulationClass=com.mitchmele.musicbrain_api.simulation.TopArtistsSimulation

# Target a different environment
./mvnw gatling:test -Pgatling -DbaseUrl=https://staging.example.com
```

Reports are generated in `target/gatling/`.

## Tech Stack

- Spring Boot 3.5
- Spring WebFlux (reactive HTTP client)
- Caffeine (caching)
- Gatling + Groovy (performance testing)
- Lombok
- JUnit 5 + Mockito
