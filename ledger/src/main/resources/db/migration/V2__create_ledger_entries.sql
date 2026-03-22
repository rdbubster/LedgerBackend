CREATE TABLE ledger_entries (
                                id BIGSERIAL PRIMARY KEY,
                                account_id BIGINT NOT NULL REFERENCES accounts(id),
                                amount NUMERIC(19, 2) NOT NULL,
                                type VARCHAR(50) NOT NULL CHECK (type IN ('CREDIT', 'DEBIT')),
                                reference_id VARCHAR(255) NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                UNIQUE (account_id, reference_id)
);