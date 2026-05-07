-- =====================================================
-- CINEMES
-- =====================================================
INSERT INTO CINEMA(ADDRESS,CITY,CINEMA_NAME,POSTAL_CODE) VALUES
('Major,15','Tarragona','Yelmo',43021);

INSERT INTO CINEMA(ADDRESS,CITY,CINEMA_NAME,POSTAL_CODE) VALUES
('Brugent, 10','Tarragona','Cinesa',43001);

INSERT INTO CINEMA(ADDRESS,CITY,CINEMA_NAME,POSTAL_CODE) VALUES
('Rambla Nova, 20','Tarragona','Ocine',43004);

-- =====================================================
-- SALES
-- =====================================================
INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 4',50,1);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
('Sala 5',50,1);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 6',80,1);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 7',70,2);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 8',50,2);

 INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 9',70,2);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 10',10,3);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 11',15,3);

INSERT INTO ROOM(NAME,CAPACITY,CINEMA_ID)VALUES
 ('Sala 12',60,3);

-- =====================================================
-- GÈNERES (catàleg inicialitzat des de SQL en arrencar)
-- =====================================================
INSERT INTO GENRE(NAME) VALUES ('Acció');         -- id 1
INSERT INTO GENRE(NAME) VALUES ('Aventura');      -- id 2
INSERT INTO GENRE(NAME) VALUES ('Comèdia');       -- id 3
INSERT INTO GENRE(NAME) VALUES ('Drama');         -- id 4
INSERT INTO GENRE(NAME) VALUES ('Thriller');      -- id 5
INSERT INTO GENRE(NAME) VALUES ('Terror');        -- id 6
INSERT INTO GENRE(NAME) VALUES ('Sci-Fi');        -- id 7
INSERT INTO GENRE(NAME) VALUES ('Animació');      -- id 8
INSERT INTO GENRE(NAME) VALUES ('Romàntic');      -- id 9
INSERT INTO GENRE(NAME) VALUES ('Documental');    -- id 10
INSERT INTO GENRE(NAME) VALUES ('Fantasia');      -- id 11
INSERT INTO GENRE(NAME) VALUES ('Misteri');       -- id 12
INSERT INTO GENRE(NAME) VALUES ('Musical');       -- id 13

-- =====================================================
-- PEL·LÍCULES
-- =====================================================
INSERT INTO MOVIE(TITLE,DESCRIPTION,DURATION,RELEASE_DATE) VALUES
('Interstellar','Un equipo viaja por el espacio para encontrar un nuevo hogar para la humanidad.',169,'2014-11-07');

INSERT INTO MOVIE(TITLE,DESCRIPTION,DURATION,RELEASE_DATE) VALUES
('Missió Impossible: Sentència Mortal','Ethan Hunt s''enfronta a una nova amenaça global en aquesta entrega plena d''acció.',163,'2023-07-12');

INSERT INTO MOVIE(TITLE,DESCRIPTION,DURATION,RELEASE_DATE) VALUES
('Inside Out 2','La Riley creix i nous emocions arriben al seu món interior en aquesta seqüela animada.',96,'2024-06-14');

-- =====================================================
-- RELACIÓ N:M MOVIE <-> GENRE
-- =====================================================
-- Interstellar (movie 1): Sci-Fi + Aventura + Drama
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (1,7);
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (1,2);
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (1,4);

-- Missió Impossible (movie 2): Acció + Aventura + Thriller
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (2,1);
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (2,2);
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (2,5);

-- Inside Out 2 (movie 3): Animació + Comèdia + Aventura
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (3,8);
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (3,3);
INSERT INTO MOVIE_GENRES(MOVIE_ID,GENRE_ID) VALUES (3,2);

-- =====================================================
-- PROJECCIONS
-- =====================================================
INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(8.50,'2026-05-02T18:30:00',1,1);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(9.00,'2026-05-02T21:30:00',1,2);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(8.75,'2026-05-03T17:00:00',1,3);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(9.50,'2026-05-03T20:15:00',1,4);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(8.25,'2026-05-04T19:30:00',1,5);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(10.00,'2026-05-05T22:00:00',1,6);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(9.50,'2026-05-09T20:00:00',2,1);

INSERT INTO SCREENING(PRICE,DATE_TIME,MOVIE_ID,ROOM_ID) VALUES
(7.50,'2026-05-10T17:00:00',3,7);
