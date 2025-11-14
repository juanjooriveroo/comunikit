FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

ENV PORT_GATEWAY=${PORT_GATEWAY}
ENV JWT_SECRET=${JWT_SECRET}
ENV JWT_EXPIRATION=${JWT_EXPIRATION}

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE ${PORT_GATEWAY}

ENTRYPOINT ["java", "-jar", "app.jar"]