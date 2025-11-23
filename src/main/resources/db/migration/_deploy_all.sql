------------------------
-- 1. TABELA: users
------------------------
IF NOT EXISTS (
    SELECT 1
    FROM sys.tables
    WHERE name = 'users'
      AND schema_id = SCHEMA_ID('dbo')
)
BEGIN
    CREATE TABLE dbo.users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name VARCHAR(120) NOT NULL,
        email VARCHAR(160) NOT NULL UNIQUE,
        password_hash VARCHAR(256) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END
GO

------------------------
-- 2. TABELA: feedbacks
------------------------
IF NOT EXISTS (
    SELECT 1
    FROM sys.tables
    WHERE name = 'feedbacks'
      AND schema_id = SCHEMA_ID('dbo')
)
BEGIN
    CREATE TABLE dbo.feedbacks (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        rating INT NOT NULL,
        title VARCHAR(200),
        message VARCHAR(2000) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_feedback_user
            FOREIGN KEY (user_id) REFERENCES dbo.users(id)
    );
END
GO

-----------------------------
-- 3. TABELA: course_progress
-----------------------------
IF NOT EXISTS (
    SELECT 1
    FROM sys.tables
    WHERE name = 'course_progress'
      AND schema_id = SCHEMA_ID('dbo')
)
BEGIN
    CREATE TABLE dbo.course_progress (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id BIGINT NOT NULL,
        track_id VARCHAR(50) NOT NULL,
        course_id VARCHAR(50) NOT NULL,
        [percent] INT NOT NULL,
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_course_progress_user
            FOREIGN KEY (user_id) REFERENCES dbo.users(id)
    );
END
GO

------------------------------
-- 4. ÍNDICES (só se não existirem)
------------------------------

-- Índice em feedbacks.user_id
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'idx_feedbacks_user_id'
      AND object_id = OBJECT_ID('dbo.feedbacks')
)
BEGIN
    CREATE INDEX idx_feedbacks_user_id
        ON dbo.feedbacks(user_id);
END
GO

-- Índice em course_progress (user_id, track_id)
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'idx_course_progress_user_track'
      AND object_id = OBJECT_ID('dbo.course_progress')
)
BEGIN
    CREATE INDEX idx_course_progress_user_track
        ON dbo.course_progress(user_id, track_id);
END
GO