create table tracks(
    id bigserial primary key,
    title varchar(120),
    album_id int,
    audio_name uuid,
    created_by int,
    duration bigint
);