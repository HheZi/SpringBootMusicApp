create table playlists(
    id serial primary key,
    name varchar(120),
    created_by int,
    image_name uuid,
    description varchar(70)
);

create table playlists_tracks(
    id serial primary key,
    playlist_id int references playlists(id),
    track_id bigint
);