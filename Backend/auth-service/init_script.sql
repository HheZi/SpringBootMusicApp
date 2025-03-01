create table refresh_tokens(
    id serial primary key,
    token uuid,
    expiration_date timestamp,
    user_id int,
    user_role varchar(50) check (user_role in ('USER', 'ADMIN'))
);