# Etapa 1: build (usa o Gradle Wrapper do projeto)
FROM gradle:8.10-jdk21 AS builder
WORKDIR /app

# Copia tudo do projeto para dentro da imagem
COPY . .

# Garante que o gradlew é executável e usa ele (mesmo Gradle e toolchains do projeto)
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

# Etapa 2: imagem final (runtime)
# Imagem de runtime (Java 17)
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia o JAR gerado pelo build (Gradle) no agente
COPY build/libs/*.jar app.jar

# Porta interna do Spring Boot
EXPOSE 8080

# Usa PORT (ACI) ou 8080 por padrão
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar ${BOOT_ARGS}"]