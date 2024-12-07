create table authors(
    id serial primary key,
    name varchar(56),
    image_name uuid,
    created_by int
);