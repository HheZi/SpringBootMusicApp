create table authors(
    id serial primary key,
    name varchar(56),
    image_name uuid,
    description varchar(360),
    created_by int
);
