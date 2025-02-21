create table tracks(
    id bigint auto_increment primary key,
    title varchar(120),
    album_id int,
    audio_name uuid,
    created_by int,
    duration bigint
);

insert into tracks(title, album_id, audio_name, created_by, duration)
values
('test', 1, '00000000-0000-0000-0000-000000000000', 1, 120),
('test2', 1, '00000000-0000-0000-0000-000000000001', 1, 120),
('test3', 2, '00000000-0000-0000-0000-000000000002', 2, 140);