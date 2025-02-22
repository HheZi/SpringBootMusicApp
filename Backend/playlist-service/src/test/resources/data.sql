create table playlists(
    id int auto_increment primary key,
    name varchar(120),
    created_by int,
    image_name uuid,
    description varchar(70)
);

create table playlists_tracks(
    id int auto_increment primary key,
    playlist_id int references playlists(id) on delete cascade,
    track_id bigint
);

insert into playlists(name, created_by, image_name, description)
values
('test', 1, '00000000-0000-0000-0000-000000000001', null),
('2test', 1, '00000000-0000-0000-0000-000000000002', 'Some description'),
('3test', 2, '00000000-0000-0000-0000-000000000003', null),
('4test', 3, null, null);

insert into playlists_tracks(playlist_id, track_id)
values
(1, 1),
(1, 2),
(2, 3),
(3, 3);