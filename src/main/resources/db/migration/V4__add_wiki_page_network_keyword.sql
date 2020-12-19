CREATE TABLE wiki_page_network_keyword
(
	wiki_page_id INT NOT NULL
		CONSTRAINT wiki_page_network_keyword_wiki_page_id_fk
			REFERENCES wiki_page,
	network_keyword TEXT NOT NULL ,
	CONSTRAINT wiki_page_network_keyword_pk
		PRIMARY KEY (wiki_page_id, network_keyword)
);
