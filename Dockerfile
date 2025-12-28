# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

# Expose port used by Render
EXPOSE 8080

# Run the app
CMD ["java", "-jar", "target/*.jar"]
