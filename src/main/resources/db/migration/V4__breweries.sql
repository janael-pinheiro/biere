CREATE TABLE breweries(id SERIAL PRIMARY KEY, name VARCHAR(255) NOT NULL, country_id INTEGER NOT NULL);
ALTER TABLE breweries ADD CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES countries (id);

ALTER TABLE beers DROP CONSTRAINT fk_country_id;
ALTER TABLE beers DROP COLUMN country_id;

ALTER TABLE beers DROP COLUMN brewery;
ALTER TABLE beers ADD COLUMN brewery_id INTEGER NOT NULL;
ALTER TABLE beers ADD CONSTRAINT fk_brewery_id FOREIGN KEY (brewery_id) REFERENCES breweries(id);