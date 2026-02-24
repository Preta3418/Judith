# build
# basically making 'jar' file, and tools to make 'jar' file is not required to be in the final image
# so it gets thrown away after the first stage. (like gradle, which is not neccesity to run the jar.)
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon

# run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
