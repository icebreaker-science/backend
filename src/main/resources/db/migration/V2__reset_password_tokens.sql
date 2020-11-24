CREATE TABLE reset_password_token (
  token varchar(100) PRIMARY KEY,
  account_id integer,
  created_at timestamp NOT NULL DEFAULT NOW(),
  FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX reset_password_token_account_idx ON reset_password_token (account_id);