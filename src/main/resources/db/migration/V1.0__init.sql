CREATE TABLE IF NOT EXISTS language  (
    id BIGSERIAL NOT NULL,
    locale VARCHAR(255),
    CONSTRAINT pk_language PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS translation (
    id BIGSERIAL NOT NULL,
    key VARCHAR(255),
    value VARCHAR(255),
    language_id int8 NOT NULL,
    CONSTRAINT pk_translation PRIMARY KEY (id),
    CONSTRAINT fk_language_translation FOREIGN KEY (language_id) REFERENCES language (id) ON UPDATE CASCADE ON DELETE CASCADE
);
