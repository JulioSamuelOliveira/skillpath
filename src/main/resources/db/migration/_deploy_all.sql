-- _deploy_all.sql  (Azure SQL / SQL Server)

-- Tabela de usuários
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

-- Tabela de feedbacks
CREATE TABLE feedbacks (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    title VARCHAR(200),
    message VARCHAR(2000) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de progresso de cursos
CREATE TABLE course_progress (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    track_id VARCHAR(50) NOT NULL,
    course_id VARCHAR(50) NOT NULL,
    [percent] INT NOT NULL,
    updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT fk_course_progress_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Índices para performance
CREATE INDEX idx_feedbacks_user_id
    ON feedbacks (user_id);

CREATE INDEX idx_course_progress_user_track
    ON course_progress (user_id, track_id);