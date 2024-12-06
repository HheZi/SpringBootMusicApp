create table albums(
    id serial primary key,
    name varchar(60),
    image_name uuid,
    album_type varchar(30),
    release_date date,
    author_id int
);

