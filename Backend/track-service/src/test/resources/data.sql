create table tracks(
    id bigint auto_increment primary key,
    title varchar(120),
    album_id int,
    audio_name uuid,
    duration bigint
);

insert into tracks(title, album_id, audio_name, duration)
values
('test', 1, '00000000-0000-0000-0000-000000000000', 120),
('2test2', 1, '00000000-0000-0000-0000-000000000001', 120),
('3test3', 2, '00000000-0000-0000-0000-000000000002', 140),
('!test!', 3, '00000000-0000-0000-0000-000000000002', 140);