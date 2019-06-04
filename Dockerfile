FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD build/libs/*.jar app.jar
CMD ["java","-jar","/app.jar"]
