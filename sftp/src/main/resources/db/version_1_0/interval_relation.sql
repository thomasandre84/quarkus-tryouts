CREATE TABLE interval_relation (
    id INT NOT NULL AUTO_INCREMENT,
    initial_interval_id INT NOT NULL,
    interval_id INT NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE interval_relation ADD CONSTRAINT uk_initial_interval_interval UNIQUE (initial_interval_id, interval_id);

ALTER TABLE interval_relation ADD CONSTRAINT fk_initial_interval FOREIGN KEY (initial_interval_id) REFERENCES initial_interval (id);
ALTER TABLE interval_relation ADD CONSTRAINT fk_interval FOREIGN KEY (interval_id) REFERENCES run_interval (id) on delete cascade;
