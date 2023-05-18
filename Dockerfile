# Stage 1: Build the application
FROM openjdk:17-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw package -DskipTests

# Stage 2: Create a runtime image
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/target/my-application.jar ./my-application.jar
EXPOSE 8080
CMD ["java", "-jar", "my-application.jar"]
