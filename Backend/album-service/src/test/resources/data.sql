create table albums(
	id int auto_increment primary key,
	name varchar(55),
	image_name uuid,
	author_id int,
	release_date date
);

INSERT INTO albums (name, image_name, author_id, release_date)
VALUES
    ('The Classic Vibes', RANDOM_UUID(), 201, '2023-10-10'),
    ('Echoes of Eternity', null, 202, '2024-01-20'),
    ('Melody Haven', null, 203, '2024-05-05'),
    ('Rhythms of Time', RANDOM_UUID(), 204, '2023-12-15'),
    ('Chords of Serenity', null, 205, '2024-07-18'),
    ('Harmonic Horizon', RANDOM_UUID(), 206, '2024-09-25'),
    ('Soulful Strings', RANDOM_UUID(), 207, '2023-08-30'),
    ('Infinite Playlist', null, 208, '2024-02-12'),
    ('Golden Grooves', null, 209, '2023-11-22'),
    ('Dynamic Harmonies', null,  210, '2024-03-10');