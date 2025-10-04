CREATE TABLE IF NOT EXISTS deals (
    id SERIAL PRIMARY KEY,
    deal_uid VARCHAR(100) NOT NULL,
    from_currency CHAR(3) NOT NULL,
    to_currency CHAR(3) NOT NULL,
    deal_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    amount NUMERIC(20,6) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT uq_deal_uid UNIQUE (deal_uid)
);