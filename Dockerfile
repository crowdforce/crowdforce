FROM openjdk:11
EXPOSE 8080
ARG JAR_FILE=cf-app/target/cf-app-1.0.0-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
