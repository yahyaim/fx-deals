CREATE TABLE IF NOT EXISTS deals (
  id SERIAL PRIMARY KEY,
  deal_id TEXT  NOT NULL UNIQUE,
  from_currency CHAR(3) NOT NULL,
  to_currency CHAR(3) NOT NULL,
  deal_timestamp TIMESTAMPTZ NOT NULL,
  deal_amount double precision NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_fxdeal_deal_id ON deals(deal_id);
