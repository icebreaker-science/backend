ALTER TABLE wiki_page 
ADD COLUMN last_altered_by_name TEXT,
ADD COLUMN last_altered_by INTEGER,
ADD CONSTRAINT wiki_page_last_altered_by
   FOREIGN KEY (last_altered_by)
   REFERENCES account;
