DROP TABLE breweries;
ALTER TABLE beers ADD COLUMN country_id VARCHAR(255);
ALTER TABLE beers ADD CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES countries(id);
ALTER TABLE beers DROP COLUMN brewery_id;
ALTER TABLE beers ADD COLUMN brewery VARCHAR(255);