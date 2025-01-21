FROM openjdk:21-jdk

COPY target/ms-books-catalogue-0.0.1.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]