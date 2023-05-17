# Stage 1: Build the application
FROM adoptopenjdk:17-jdk-hotspot AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

# Stage 2: Create a runtime image
FROM adoptopenjdk:17-jre-hotspot
WORKDIR /app
COPY --from=build /app/target/my-application.jar ./my-application.jar
EXPOSE 8080
CMD ["java", "-jar", "my-application.jar"]
