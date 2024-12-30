create table albums(
	id int primary key,
	name varchar(55),
	image_name uuid,
	created_by int,
	author_id int,
	release_date date
);

INSERT INTO albums
VALUES
    (1, 'The Classic Vibes', RANDOM_UUID(), 1, 201, '2023-10-10'),
    (2, 'Echoes of Eternity', null, 2, 202, '2024-01-20'),
    (3, 'Melody Haven', RANDOM_UUID(), 3, 203, '2024-05-05'),
    (4, 'Rhythms of Time', RANDOM_UUID(), 4, 204, '2023-12-15'),
    (5, 'Chords of Serenity', null, 5, 205, '2024-07-18'),
    (6, 'Harmonic Horizon', RANDOM_UUID(), 6, 206, '2024-09-25'),
    (7, 'Soulful Strings', RANDOM_UUID(), 7, 207, '2023-08-30'),
    (8, 'Infinite Playlist', null, 8, 208, '2024-02-12'),
    (9, 'Golden Grooves', null, 9, 209, '2023-11-22'),
    (10, 'Dynamic Harmonies', RANDOM_UUID(), 10, 210, '2024-03-10');