FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

ENV PORT_AUTH=${PORT_AUTH}
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}
ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_NAME=auth_service
ENV DB_USER=${POSTGRES_USER}
ENV DB_PASSWORD=${POSTGRES_PASSWORD}
ENV KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
ENV KAFKA_CONSUMER_GROUP=${KAFKA_CONSUMER_GROUP}

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE ${PORT_AUTH}

ENTRYPOINT ["java", "-jar", "app.jar"]