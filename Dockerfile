# Etapa 1: build (Gradle dentro da imagem)
FROM gradle:8.10-jdk17 AS builder
WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon

# Etapa 2: imagem final
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia o JAR gerado pelo Gradle
COPY --from=builder /app/build/libs/*.jar app.jar

# Porta exposta
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar ${BOOT_ARGS}"]