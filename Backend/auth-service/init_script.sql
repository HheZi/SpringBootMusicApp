create table refresh_tokens(
    id serial primary key,
    token uuid,
    expiration_date timestamp,
    user_id int
);