CREATE TABLE contents (

    id CHAR(36) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    summary TEXT,
    content_type VARCHAR(20) NOT NULL,
    text_content LONGTEXT,
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    file_format VARCHAR(20),
    category VARCHAR(100),
    subcategory VARCHAR(100),
    confidence DECIMAL(5,4),
    model_version VARCHAR(50),
    keywords JSON,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);