CREATE TABLE idempotency_keys (
                                  id BIGSERIAL PRIMARY KEY,
                                  account_id BIGINT NOT NULL,
                                  reference_id VARCHAR(255) NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                  UNIQUE (account_id, reference_id)
);