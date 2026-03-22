CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL
);