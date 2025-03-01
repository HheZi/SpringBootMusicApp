create table users(
    id serial primary key,
    username varchar(50),
    email varchar(60),
    password text,
    user_role varchar(50) CHECK (user_role IN ('USER', 'ADMIN'))
);