create table refresh_tokens(
    id int auto_increment primary key,
    token uuid,
    expiration_date timestamp,
    user_id int
);
