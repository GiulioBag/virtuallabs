FROM openjdk:11
WORKDIR /home
COPY /target/virtuallabs-0.0.1-SNAPSHOT.jar app.jar
COPY application.prod.properties application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
