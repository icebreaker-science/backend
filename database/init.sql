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
