FROM maven:latest AS build

WORKDIR /app

# Dependencies zuerst kopieren (besseres Caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Source code kopieren und builden
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Non-root user f  r Security
RUN addgroup --system spring && adduser --system --group spring
USER spring:spring

# JAR aus build stage kopieren
COPY --from=build /app/target/untis-webservice-1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]