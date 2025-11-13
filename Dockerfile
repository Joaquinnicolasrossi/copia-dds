# Etapa 1: build
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests dependency:copy-dependencies

# Etapa 2: runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiar los artefactos desde la etapa anterior
COPY --from=builder /build/target/metamapa.jar app.jar
COPY --from=builder /build/target/dependency ./dependency

EXPOSE 7000
CMD sh -c "java -cp 'app.jar:dependency/*' Main"
