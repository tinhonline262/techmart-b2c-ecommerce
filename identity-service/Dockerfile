# Stage 1 - Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy only pom.xml to leverage Docker cache
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./

# This step will cache dependencies as long as pom.xml hasn't changed
RUN ./mvnw dependency:resolve

# Copy source code only after dependencies are resolved
COPY src ./src

# Package the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime Image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar file from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional - usually 8160)
EXPOSE 8160

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
