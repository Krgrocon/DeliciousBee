FROM openjdk:21
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9000
ENTRYPOINT ["java","-jar","/app.jar"]