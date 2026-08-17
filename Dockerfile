FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -B dependency:go-offline || true

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -q clean verify

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN groupadd -r hotelsearch && useradd -r -g hotelsearch hotelsearch

COPY --from=build /build/target/challenge-mindata.jar /app/app.jar

EXPOSE 8080

USER hotelsearch

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
