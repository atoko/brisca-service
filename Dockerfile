FROM gradle:5.4-jdk11 as builder
USER root
WORKDIR /builder
ADD . /builder
RUN gradle build --stacktrace

FROM openjdk:11.0.3-slim
WORKDIR /app
EXPOSE 8080
COPY --from=builder /builder/build/libs/brisca-core-0.0.1.jar .
CMD ["java", "-jar", "brisca-core-0.0.1.jar"]