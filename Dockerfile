# ============================================
# STAGE 1 - BUILD DO JAR (GRADLE + JAVA 21)
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia arquivos do Gradle
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Copia o código-fonte
COPY src ./src

# Gera o jar da aplicação
RUN chmod +x ./gradlew && ./gradlew clean bootJar --no-daemon

# ============================================
# STAGE 2 - RUNTIME (JRE)
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o jar gerado
COPY --from=builder /app/build/libs/*.jar app.jar

# Porta padrão do Spring Boot (vai ler PORT=8080 do ambiente)
EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]