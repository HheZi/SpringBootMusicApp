create table albums(
	id int auto_increment primary key,
	name varchar(55),
	image_name uuid,
	created_by int,
	author_id int,
	release_date date
);

INSERT INTO albums (name, image_name, created_by, author_id, release_date)
VALUES
    ('The Classic Vibes', RANDOM_UUID(), 1, 201, '2023-10-10'),
    ('Echoes of Eternity', RANDOM_UUID(), 2, 202, '2024-01-20'),
    ('Melody Haven', RANDOM_UUID(), 3, 203, '2024-05-05'),
    ('Rhythms of Time', RANDOM_UUID(), 4, 204, '2023-12-15'),
    ('Chords of Serenity', null, 5, 205, '2024-07-18'),
    ('Harmonic Horizon', RANDOM_UUID(), 6, 206, '2024-09-25'),
    ('Soulful Strings', RANDOM_UUID(), 7, 207, '2023-08-30'),
    ('Infinite Playlist', null, 8, 208, '2024-02-12'),
    ('Golden Grooves', null, 9, 209, '2023-11-22'),
    ('Dynamic Harmonies', RANDOM_UUID(), 10, 210, '2024-03-10');