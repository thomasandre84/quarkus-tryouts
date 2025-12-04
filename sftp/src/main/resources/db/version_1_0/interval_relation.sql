CREATE TABLE interval_relation (
    id INT,
    initial_interval_id INT NOT NULL,
    interval_id INT NOT NULL
);
ALTER TABLE interval_relation ADD CONSTRAINT interval_relation_pkey PRIMARY KEY (id);
ALTER TABLE interval_relation ADD CONSTRAINT uk_initial_interval_interval UNIQUE (initial_interval_id, interval_id);

ALTER TABLE interval_relation ADD CONSTRAINT fk_initial_interval FOREIGN KEY (initial_interval_id) REFERENCES initial_interval (id);
ALTER TABLE interval_relation ADD CONSTRAINT fk_interval FOREIGN KEY (interval_id) REFERENCES interval (id) on delete cascade;
