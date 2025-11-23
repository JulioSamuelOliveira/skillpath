<<<<<<< HEAD
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
=======
# ============================
# Etapa 1: build (Gradle)
# ============================
FROM gradle:8.10-jdk21 AS builder
WORKDIR /app

# Copia tudo (código + gradle wrapper, etc.)
COPY . .

# Gera o jar da aplicação
RUN gradle clean bootJar --no-daemon

# ============================
# Etapa 2: imagem final (runtime)
# ============================
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o jar gerado do stage de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta interna do container
EXPOSE 8080

# ENTRYPOINT:
# - Usa JAVA_OPTS se você quiser passar algo via env
# - PORT vem do ambiente (Azure CLI/ACI) e, se não vier, usa 8080
>>>>>>> 3f928fc2c856077e4134b43575160bc208c90e8f
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar ${BOOT_ARGS}"]
