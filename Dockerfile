FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=/build/libs/*.jar
COPY /src/main/resources/udemy-free-courses-verifier-firebase-adminsdk-6e1td-d2597deec6.json /var/udemy-free-courses-verifier-firebase-adminsdk-6e1td-d2597deec6.json
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]