# Hotel Search App

Service to register hotel availability searches and check how many times an exact search was repeated. Built with Spring Boot 3.5 + Java 21, hexagonal architecture, Kafka to decouple the persistence, and Oracle as the database.

## Run instructions:

Docker and Docker compose is needed. All building process happens inside the container.

```bash
docker compose up --build
```

This run 3 services: Oracle (port 1521), Kafka (port 9092) and the spring app (port 8080). 

To bring it down:
```bash
docker compose down
docker compose down -v   # also wipes Oracle's data
```

## Endpoints:

```bash
curl -X POST http://localhost:8080/search \
  -H "Content-Type: application/json" \
  -d '{"hotelId":"1234aBc","checkIn":"29/12/2023","checkOut":"31/12/2023","ages":[30,29,1,3]}'

curl "http://localhost:8080/count?searchId=searchId"
```

Swagger: `http://localhost:8080/swagger-ui.html`.

### POST /search
Registers a search. Validates the payload (dates, ages, blank fields) and returns a `searchId` right away. It doesn't save to the database synchronously though — it publishes the event to Kafka and responds immediately, a separate consumer takes care of persisting it in the background.

### GET /count?searchId=xxx
Returns the original search and how many times it was repeated exactly (same hotel, dates and ages, in the same order). It can return 404 if you query too fast right after the POST, since it hasn't been saved yet — that's on purpose: for this endpoint we prioritize latency over strict consistency, no point adding retries here.

## Checking the database

While docker-compose is running you can connect to Oracle DB with these credentials:

```
host: localhost
port: 1521
service: MINDATA_CHALLENGE_DB
user: hotel_search
password: hotel_search
```

Or by console:
```bash
docker exec -it hotel-search-oracle sqlplus hotel_search/hotel_search@//localhost:1521/MINDATA_CHALLENGE_DB
```

## Architecture

It's hexagonal:  
- `domain` package doesn't depend on Spring/Kafka/JPA, just plain Java. 
- `application` only knows and can use classes of  domain. 
- `infrastructure` is the layer that interact with Kafka, the api REST and the database, split into inbound adapters (REST) and outbound ones (Kafka producer/consumer, persistence). 
This allows us to swapped for another database or another message broker, without touching business logic.

## Tests

- Unit tests for the domain and application services (with Mockito and JUnit), 
- Controller tests with MockMvc, 
- Persistence tests with DataJpaTest, 
- Separate tests for the Kafka producer/consumer
- And a integration test that spin up an in-memory database service and a local Kafka broker.

```bash
mvn clean test      # runs the tests
mvn clean verify     # also enforces 80% coverage with JaCoCo
```

The coverage report: `target/site/jacoco/index.html`.