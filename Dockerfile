## Build app ##

FROM openjdk:14-alpine AS builder
WORKDIR /build/

COPY . .
RUN ./gradlew clean build -x test


## Run server ##

FROM openjdk:14-alpine AS server
WORKDIR /server/

COPY --from=builder /build/build/libs /server

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/server/icebreaker-science.jar"]
