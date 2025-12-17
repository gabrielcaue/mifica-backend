# Etapa 1: build com Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw clean package -DskipTests

# Etapa 2: imagem final leve
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
