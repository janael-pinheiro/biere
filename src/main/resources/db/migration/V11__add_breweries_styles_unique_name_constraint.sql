ALTER TABLE breweries ADD CONSTRAINT unique_brewery_name UNIQUE(name);
ALTER TABLE styles ADD CONSTRAINT unique_style_name UNIQUE(name);