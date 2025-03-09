create table users(
    id serial primary key,
    username varchar(50),
    email varchar(60),
    password text,
    user_role varchar(50) CHECK (user_role IN ('USER', 'ADMIN'))
);

--Login: 'Admin', password: 'admin'
insert into users (username, email, password, user_role)
values 
('Admin', 'admin@gmail.com', '$2a$10$bMg323NxR9SAY6DNnt6cNuYKYq6TxzxqbG/qxGxiarTRTBl4.jY/a', 'ADMIN');