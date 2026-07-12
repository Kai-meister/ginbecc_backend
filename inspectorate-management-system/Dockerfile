# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

# Budget for the 512MB instance: ~230MB heap + 192MB metaspace + stacks/
# native stays under the container limit. Unbounded heap (75%) wedged the
# container at 511MB (502s); a 128m metaspace cap OOMed springdoc.
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=45.0", "-XX:MaxMetaspaceSize=192m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
