# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy build definition first for better layer caching
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
# Normalize line endings (robust when built from a Windows checkout) and make executable
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Copy sources and build the boot jar
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Default to the zero-setup demo profile; override with SPRING_PROFILES_ACTIVE=ai
ENV SPRING_PROFILES_ACTIVE=demo
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
