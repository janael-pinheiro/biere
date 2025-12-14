INSERT INTO countries(name,created_at) VALUES('Belgium','2025-10-29 12:36:21.919307-03'),
                                             ('Netherlands','2025-10-29 12:36:21.919307-03'),
                                             ('Denmark','2025-10-29 12:36:21.919307-03'),
                                             ('Greece','2025-10-29 12:36:21.919307-03'),
                                             ('Brazil', '2025-10-29 12:36:21.919307-03'),
                                             ('Argentina', '2025-10-29 12:36:21.919307-03'),
                                             ('Germany', '2025-10-29 12:36:21.919307-03'),
                                             ('USA', '2025-10-29 12:36:21.919307-03'),
                                             ('Mexico', '2025-10-29 12:36:21.919307-03');
INSERT INTO styles(name) VALUES('amber'),
                               ('lager'),
                               ('premium lager'),
                               ('amber lager'),
                               ('IPA'),
                               ('lager strong');
INSERT INTO breweries (name,country_id) VALUES
                                                   ('Brasserie N.V. Palm',1),
                                                   ('Heineken Company',2),
                                                   ('Harboes Bryggeri',3),
                                                   ('Athenian Brewery',4),
                                                   ('Baden Baden',5),
                                                   ('Patagonia',6);
INSERT INTO beers (name,alcohol_content,brewery_id,style_id,"year") VALUES
                                                                               ('Palm',5.199999809265137,1,1,2003),
                                                                               ('Heineken',5.0,2,2,1873),
                                                                               ('Amstel',4.6,2,2,1870),
                                                                               ('Birra Moretti',4.6,2,3,1860),
                                                                               ('Viiking 12%',4.6,3,4,1974),
                                                                               ('Alfa (Αλφα) Strong',7.0,4,5,1961),
                                                                               ('Cristal',4.5,5,2,2000),
                                                                               ('Patagonia',5.800000190734863,6,5,2007);
