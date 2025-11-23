# Etapa 1: build (usa o Gradle Wrapper do projeto)
FROM gradle:8.10-jdk21 AS builder
WORKDIR /app

# Copia tudo do projeto para dentro da imagem
COPY . .

# Garante que o gradlew é executável e usa ele (mesmo Gradle e toolchains do projeto)
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

# Etapa 2: imagem final (runtime)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o JAR gerado na etapa de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta do Spring Boot
EXPOSE 8080

# Sobe a aplicação usando a PORT (Azure ACI) ou 8080 como default
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar ${BOOT_ARGS}"]