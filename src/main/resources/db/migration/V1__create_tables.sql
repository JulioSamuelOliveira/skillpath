-- V1__create_tables.sql

-- Tabela de usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Tabela de feedbacks
CREATE TABLE feedbacks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    title VARCHAR(200),
    message VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela de progresso de cursos
CREATE TABLE course_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    track_id VARCHAR(50) NOT NULL,
    course_id VARCHAR(50) NOT NULL,
    percent INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_course_progress_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Índices para performance
CREATE INDEX idx_feedbacks_user_id
    ON feedbacks (user_id);

CREATE INDEX idx_course_progress_user_track
    ON course_progress (user_id, track_id);
