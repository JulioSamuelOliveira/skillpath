# SkillPath – API Java (Spring Boot + PostgreSQL + JWT)

API em **Java 21 / Spring Boot 3** para o projeto **SkillPath**, responsável por autenticação, cadastro de usuários e acompanhamento de progresso em trilhas de estudo.  
Ela foi pensada para ser consumida pelo front-end em TypeScript (mobile/web), expondo endpoints REST protegidos com **JWT**.

---

## ⚙️ Tecnologias principais

- **Java 21**
- **Spring Boot 3.5**
- **Gradle**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security (JWT)**
- **PostgreSQL**
- **Flyway** (migrações de banco)
- **Docker Compose** (subir Postgres em dev)

---

## 👥 Participantes

- Julio Samuel De Oliveira — RM557453  
- Bruno Da Silva Souza — RM94346 
- Leonardo Da Silva Pereira — RM557598

---

## 🗄️ Banco de dados

Banco: **PostgreSQL**

As tabelas principais criadas pelo Flyway:

- `users` – informações de login e identificação
- `feedbacks` – feedback do usuário sobre a experiência
- `course_progress` – controle de progresso em cada trilha/curso

A migração inicial está em `src/main/resources/db/migration/V1__create_tables.sql`.

---

## 🔐 Autenticação e Autorização

A API utiliza **JWT (JSON Web Token)**:

- O usuário se registra em `/api/auth/signup` ou faz login em `/api/auth/login`.
- A resposta retorna um **token JWT** e os dados básicos do usuário.
- Em chamadas autenticadas, o token é enviado no header:

```http
Authorization: Bearer <seu_token_jwt>
```

O `JwtAuthenticationFilter` valida o token em cada requisição e popula o `SecurityContext`.  
As regras de segurança são configuradas em `SecurityConfig.java`.

---

## ⚙️ Configuração (`application.properties`)

Exemplo de configuração típica para ambiente local:

```properties
# Porta padrão
server.port=8080

# PostgreSQL (usando docker-compose)
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=myuser
spring.datasource.password=mypassword

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway
spring.flyway.enabled=true

# JWT
jwt.secret=uma-chave-bem-grande-e-aleatoria-para-o-jwt-1234567890
jwt.expiration-ms=86400000   # 24 horas em milissegundos
```

> Em produção, o ideal é ler `jwt.secret` de variável de ambiente, e não deixar a chave fixa no arquivo.

---

## 🐳 Subindo Postgres com Docker Compose

O projeto inclui um `compose.yaml` que sobe o Postgres já com banco/usuário/senha.

Na raiz do projeto:

```bash
docker compose up -d
```

Isso cria um container Postgres acessível a partir da aplicação Java.

Para parar:

```bash
docker compose down
```

---

## ▶️ Como rodar a API localmente

### 1. Pré-requisitos

- Java 21
- Gradle (ou wrapper `./gradlew`)
- Docker + Docker Compose (para subir o Postgres)  

### 2. Passo a passo

1. Subir o banco:

   ```bash
   docker compose up -d
   ```

2. Ajustar o `application.properties` com a URL/credenciais do Postgres e o segredo do JWT.

3. Rodar a aplicação:

   ```bash
   ./gradlew bootRun
   ```

   Ou via IDE (VS Code / IntelliJ) executando `SkillPathApplication`.

4. A API ficará disponível em:

   - `http://localhost:8080`

---

## 🧪 Endpoints principais

### Autenticação

#### `POST /api/auth/signup`

Registra um novo usuário.

**Request body**

```json
{
  "name": "Felipe",
  "email": "felipe@example.com",
  "password": "123456"
}
```

**Response (201)**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "Felipe",
    "email": "felipe@example.com"
  }
}
```

---

#### `POST /api/auth/login`

Autentica um usuário existente.

**Request body**

```json
{
  "email": "felipe@example.com",
  "password": "123456"
}
```

**Response (200)**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "Felipe",
    "email": "felipe@example.com"
  }
}
```

---

#### `GET /api/auth/me`

Retorna os dados do usuário autenticado.

**Headers**

```http
Authorization: Bearer <token>
```

**Response (200)**

```json
{
  "id": 1,
  "name": "Felipe",
  "email": "felipe@example.com"
}
```

---

### Feedbacks / Progresso

> Os nomes podem variar de acordo com o código final, mas em geral seguem esse padrão:

- `POST /api/feedbacks` – envia feedback do usuário
- `GET  /api/feedbacks` – lista feedbacks
- `POST /api/course-progress` – atualiza progresso de um curso/trilha
- `GET  /api/course-progress` – consulta progresso do usuário

Todas essas rotas são protegidas por JWT (`Authorization: Bearer <token>`).

---

## 🌐 Integração com o front-end (TypeScript / mobile)

O front-end mobile em TypeScript consome diretamente essa API:

- Na tela de login/registro: chama `/api/auth/signup` e `/api/auth/login`.
- Armazena o token JWT (por exemplo, AsyncStorage).
- Em todas as chamadas autenticadas, adiciona o header:

  ```ts
  axios.get('/api/auth/me', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  ```

---

## 📦 Build para produção

Gerar o JAR:

```bash
./gradlew clean build
```

O artefato ficará em `build/libs/skillpath-<versão>.jar`.

Para rodar:

```bash
java -jar build/libs/skillpath-*.jar
```

Lembre de configurar as variáveis de ambiente / propriedades em produção:

```bash
export SPRING_DATASOURCE_URL=...
export SPRING_DATASOURCE_USERNAME=...
export SPRING_DATASOURCE_PASSWORD=...
export JWT_SECRET=uma-chave-forte-aqui
export JWT_EXPIRATION_MS=86400000
```

