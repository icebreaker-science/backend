DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS post;

CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL
);

CREATE TABLE account_role (
  email TEXT NOT NULL,
  role TEXT NOT NULL,
  PRIMARY KEY (email, role),
  FOREIGN KEY (email) REFERENCES account (email) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX account_role_email_index ON account_role (email);

CREATE TABLE profile (
  account_id INTEGER PRIMARY KEY,
  title TEXT,
  forename TEXT NOT NULL,
  surname TEXT NOT NULL,
  institution TEXT,
  city TEXT,
  research_area TEXT,
  FOREIGN KEY (account_id) REFERENCES account (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE post (
  id SERIAL PRIMARY KEY,
  owner INTEGER NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  title TEXT NOT NULL,
  FOREIGN KEY (owner) REFERENCES account (id) ON UPDATE CASCADE ON DELETE CASCADE
);
