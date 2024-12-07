create table refresh_tokens(
    id serial primary key,
    token uuid,
    expiration_date date,
    user_id int
);