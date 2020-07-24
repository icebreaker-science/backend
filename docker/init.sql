DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS post;

CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  title TEXT,
  forename TEXT NOT NULL,
  surname TEXT NOT NULL,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL,
  institution TEXT,
  city TEXT,
  research_area TEXT
);

CREATE TABLE post (
  id SERIAL PRIMARY KEY,
  owner INTEGER NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  title TEXT NOT NULL,
  FOREIGN KEY (owner) REFERENCES account (id)
);