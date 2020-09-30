CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  is_enabled boolean NOT NULL DEFAULT false
);

create table account_confirmation
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    confirmation_token character varying(255) COLLATE pg_catalog."default",
    created_date timestamp without time zone,
    account_id integer NOT NULL,
    CONSTRAINT account_confirmation_pkey PRIMARY KEY (id),
    CONSTRAINT account_confirmation_account_id_fkey FOREIGN KEY (account_id)
        REFERENCES account (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE account_role (
  email TEXT NOT NULL,
  role TEXT NOT NULL,
  PRIMARY KEY (email, role),
  FOREIGN KEY (email) REFERENCES account (email) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX account_role_email_index ON account_role (email);

CREATE TABLE account_profile (
  account_id INTEGER PRIMARY KEY,
  title TEXT,
  forename TEXT NOT NULL,
  surname TEXT NOT NULL,
  institution TEXT,
  city TEXT,
  research_area TEXT,
  FOREIGN KEY (account_id) REFERENCES account (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE wiki_page (
  id SERIAL PRIMARY KEY,
  type TEXT NOT NULL,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  reference TEXT
);


/* JUSTIFICATION. Postal codes are encoded as text as they convey no semantic
 * meaning as numbers. 
 * NOTE. The first 2 characters reference a specific area
*/
CREATE TABLE device_availability (
  id SERIAL PRIMARY KEY,
  device_id INTEGER NOT NULL,
  comment TEXT,
  german_postal_code TEXT,
  institution TEXT NOT NULL,
  research_group TEXT,
  account_id INTEGER NOT NULL,
  FOREIGN KEY (account_id) REFERENCES account (id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (device_id) REFERENCES wiki_page (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX device_availability_device_id ON device_availability (device_id);


CREATE TABLE paper (
    icebreaker_id SERIAL PRIMARY KEY,
    doi TEXT,
    core_id TEXT,
    title TEXT,
    abstract TEXT,
    has_full_text BOOLEAN,
    year INTEGER,
    topics JSONB,
    subjects JSONB,
    language_detected_most_likely TEXT,
    language_detected_probabilities JSONB
);

CREATE INDEX papers_doi_idx ON paper (doi);
